package snownee.fruits.bee;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFPlayer;

public class HauntingManager {
	@Nullable
	public final Entity target;
	public final boolean isGhostBee;
	public CompoundTag storedBee;
	private ImmutableSet<Trait> traits = ImmutableSet.of();
	private int fireCounter;
	private long lastDamage;
	private long ticks;
	private int advancementCounter;

	public HauntingManager(@Nullable Entity target) {
		this.target = target;
		isGhostBee = target instanceof Bee && BeeAttributes.of(target).hasTrait(Trait.GHOST);
		if (isGhostBee) {
			target.stopRiding();
		}
	}

	public void hurtInFire(ServerPlayer player) {
		if (player.level().getGameTime() - lastDamage < 30) {
			if (++fireCounter >= 4) {
				getExorcised(player);
			}
		} else {
			fireCounter = 0;
		}
		lastDamage = player.level().getGameTime();
	}

	public void getExorcised(ServerPlayer player) {
		player.setCamera(null);
		if (isGhostBee) {
			addNegativeEffects((LivingEntity) target);
		}
		respawnStoredBee(player);
		addNegativeEffects(player);
		player.level().playSound(null, player, BeeModule.STOP_HAUNTING.get(), player.getSoundSource(), 1, 1);
	}

	private static void addNegativeEffects(LivingEntity entity) {
		if (FFCommonConfig.hauntingCooldownSeconds <= 0) {
			return;
		}
		int ticks = FFCommonConfig.hauntingCooldownSeconds * 20;
		entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, 1));
		entity.addEffect(new MobEffectInstance(BeeModule.FRAGILITY.get(), ticks, 1));
	}

	public void tick(ServerPlayer player) {
		if (target == null) {
			getExorcised(player);
			((FFPlayer) player).fruits$ensureCamera();
			return;
		}
		if (++ticks > FFCommonConfig.hauntingGhostBeeTimeLimitTicks && FFCommonConfig.hauntingGhostBeeTimeLimitTicks > 0 && isGhostBee) {
			getExorcised(player);
		}
	}

	public void respawnStoredBee(ServerPlayer player) {
		if (player.level().isClientSide || storedBee == null) {
			return;
		}
		EntityType.create(storedBee, player.level()).ifPresent(entity -> {
			entity.setPos(player.getX(), player.getY() + 0.7, player.getZ());
			addNegativeEffects((LivingEntity) entity);
			player.serverLevel().addWithUUID(entity);
		});
		storedBee = null;
		traits = ImmutableSet.of();
	}

	public void storeBee(Bee bee) {
		if (bee.level().isClientSide) {
			return;
		}
		traits = ImmutableSet.copyOf(BeeAttributes.of(bee).getGenes().getTraits());
		storedBee = new CompoundTag();
		storedBee.putString("id", bee.getEncodeId());
		bee.saveWithoutId(storedBee);
		bee.discard();
	}

	public boolean hasTrait(Trait trait) {
		return traits.contains(trait);
	}

	public void performPinkSkill() {
		if (advancementCounter == 0 && target != null && target.getType() == EntityType.RAVAGER) {
			advancementCounter = 1;
		}
	}

	public void onRavagerKill(Player player) {
		if (advancementCounter > 0 && ++advancementCounter == 6) {
			Hooks.awardSimpleAdvancement(player, "haunting_skill");
		}
	}
}
