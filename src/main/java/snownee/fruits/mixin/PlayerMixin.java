package snownee.fruits.mixin;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.network.SHauntPacket;
import snownee.fruits.bee.network.SSyncPlayerPacket;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;

@Mixin(Player.class)
public abstract class PlayerMixin implements FFPlayer {
	@Unique
	private Map<String, GeneName> geneNames = Map.of();
	@Unique
	private HauntingManager hauntingManager;

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		if (!geneNames.isEmpty()) {
			ListTag list = new ListTag();
			for (Map.Entry<String, GeneName> entry : geneNames.entrySet()) {
				ListTag nameTag = new ListTag();
				nameTag.add(StringTag.valueOf(entry.getKey()));
				nameTag.add(StringTag.valueOf(entry.getValue().name()));
				nameTag.add(StringTag.valueOf(entry.getValue().desc()));
				list.add(nameTag);
			}
			compoundTag.put("FruitfulFun:GeneNames", list);
			compoundTag.putString("FruitfulFun:GeneticsDifficulty", FFCommonConfig.geneticsDifficulty.name());
		}
		if (hauntingManager != null && hauntingManager.storedBee != null) {
			compoundTag.put("FruitfulFun:StoredBee", hauntingManager.storedBee);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		if (compoundTag.contains("FruitfulFun:GeneNames")) {
			for (Tag e : compoundTag.getList("FruitfulFun:GeneNames", Tag.TAG_LIST)) {
				ListTag nameTag = (ListTag) e;
				String code = nameTag.getString(0);
				String name = nameTag.getString(1);
				String desc = nameTag.getString(2);
				fruits$setGeneName(code, new GeneName(name, desc));
			}
		}
		if (FFCommonConfig.geneticsDifficulty == FFCommonConfig.GeneticsDifficulty.Easy) {
			FFCommonConfig.GeneticsDifficulty difficulty = null;
			if (compoundTag.contains("FruitfulFun:GeneticsDifficulty")) {
				try {
					difficulty = FFCommonConfig.GeneticsDifficulty.valueOf(compoundTag.getString("FruitfulFun:GeneticsDifficulty"));
				} catch (Throwable ignored) {
				}
			}
			if (difficulty != FFCommonConfig.geneticsDifficulty) {
				for (Allele allele : Allele.sortedByCode()) {
					String code = String.valueOf(allele.codename);
					GeneName geneName = geneNames.get(code);
					if (geneName != null && geneName.name().equals(code)) {
						fruits$setGeneName(code, new GeneName(allele.name, ""));
					}
				}
			}
		}
		if (compoundTag.contains("FruitfulFun:StoredBee")) {
			hauntingManager = new HauntingManager(null);
			hauntingManager.storedBee = compoundTag.getCompound("FruitfulFun:StoredBee");
		}
	}

	@Override
	public String fruits$getGeneName(String code) {
		if (geneNames.containsKey(code)) {
			return geneNames.get(code).name();
		}
		return code;
	}

	@Override
	public String fruits$getGeneDesc(String code) {
		if (geneNames.containsKey(code)) {
			return geneNames.get(code).desc();
		}
		return "";
	}

	@Override
	public void fruits$setGeneName(String code, GeneName name) {
		if (geneNames.isEmpty()) {
			geneNames = Maps.newHashMapWithExpectedSize(Allele.values().size());
		}
		geneNames.put(code, name);
	}

	@Override
	public Map<String, GeneName> fruits$getGeneNames() {
		return geneNames;
	}

	@Override
	public void fruits$setGeneNames(Map<String, GeneName> geneNames) {
		if (geneNames.isEmpty()) {
			this.geneNames = Map.of();
		} else {
			this.geneNames = Maps.newHashMapWithExpectedSize(geneNames.size());
			this.geneNames.putAll(geneNames);
		}
	}

	@Override
	public void fruits$maybeInitGenes() {
		boolean changed = false;
		for (Allele allele : Allele.sortedByCode()) {
			String code = String.valueOf(allele.codename);
			if (!geneNames.containsKey(code)) {
				if (FFCommonConfig.geneticsDifficulty == FFCommonConfig.GeneticsDifficulty.Easy) {
					fruits$setGeneName(code, new GeneName(allele.name, ""));
				} else {
					fruits$setGeneName(code, new GeneName(code, ""));
				}
				changed = true;
			}
		}
		if (changed) {
			SSyncPlayerPacket.send((ServerPlayer) (Object) this);
		}
	}

	@Override
	public void fruits$setHauntingTarget(Entity target) {
		if (target == fruits$hauntingTarget()) {
			return;
		}
		Player player = (Player) (Object) this;
		if (player instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isChangingDimension()) {
				SHauntPacket.send(serverPlayer, target);
			}
			if (target == player) {
				if (hauntingManager != null) {
					hauntingManager.getExorcised(serverPlayer);
				}
			} else {
				player.level().playSound(null, player, BeeModule.START_HAUNTING.get(), player.getSoundSource(), 1, 1);
				player.setXRot(0);
				player.setYRot(0);
				serverPlayer.setCamera(target);
				if (target instanceof FFLivingEntity entity) {
					entity.fruits$setHauntedBy(player.getUUID());
				}
			}
			if (fruits$hauntingTarget() instanceof FFLivingEntity former) {
				former.fruits$setHauntedBy(null);
			}
		}
//		FruitfulFun.LOGGER.info("Haunting target set to {}", target);
		hauntingManager = target == player ? null : new HauntingManager(target);
	}

	@Override
	@Nullable
	public Entity fruits$hauntingTarget() {
		return hauntingManager == null ? null : hauntingManager.target;
	}

	@Override
	@Nullable
	public HauntingManager fruits$hauntingManager() {
		return hauntingManager;
	}

	@Override
	public boolean fruits$isHaunting() {
		return fruits$hauntingTarget() != null;
	}

	@Override
	public void fruits$ensureCamera() {
		if ((Object) this instanceof ServerPlayer player) {
			Entity target = fruits$hauntingTarget();
			if (target == null) {
				target = player;
			}
			SHauntPacket.send(player, target);
			player.connection.send(new ClientboundSetCameraPacket(target));
			player.connection.resetPosition();
		}
	}
}
