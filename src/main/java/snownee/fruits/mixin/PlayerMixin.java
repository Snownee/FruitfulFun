package snownee.fruits.mixin;

import java.util.Map;

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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.network.SSyncPlayerPacket;
import snownee.fruits.duck.FFPlayer;

@Mixin(Player.class)
public class PlayerMixin implements FFPlayer {
	@Unique
	private Map<String, GeneName> geneNames = Map.of();

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
				fruits$setGeneName(code, new GeneName(code, ""));
				changed = true;
			}
		}
		if (changed) {
			SSyncPlayerPacket.send((ServerPlayer) (Object) this);
		}
	}

}
