package snownee.fruits.mixin.haunt;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.util.ClientProxy;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements FFLivingEntity {
	@Shadow
	protected int lerpSteps;

	@Shadow
	protected abstract boolean isImmobile();

	@Shadow
	protected abstract void serverAiStep();

	@Unique
	@Nullable
	private UUID hauntedBy;
	@Unique
	private int pinkGlowing;

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = {"isPickable", "isPushable", "canBeSeenByAnyone"}, at = @At("HEAD"), cancellable = true)
	private void isPickable(CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && this instanceof FFPlayer player && player.fruits$isHaunting()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
	private void aiStep(CallbackInfo ci) {
		if (!Hooks.bee) {
			return;
		}
		if (pinkGlowing > 0) {
			pinkGlowing--;
		}
		fruits$getHauntedBy(); // remove invalid spectatedBy
		if (this instanceof FFPlayer player && player.fruits$isHaunting()) {
			if (isControlledByLocalInstance()) {
				lerpSteps = 0;
				syncPacketPositionCodec(getX(), getY(), getZ());
			}
			if (!isImmobile() && isEffectiveAi()) {
				level().getProfiler().push("newAi");
				serverAiStep();
				level().getProfiler().pop();
			}
			ci.cancel();
		}
	}

	@Override
	@Nullable
	public Player fruits$getHauntedBy() {
		if (hauntedBy == null) {
			if (level().isClientSide && ClientProxy.getPlayer() instanceof FFPlayer player && player.fruits$hauntingTarget() == this) {
				return (Player) player;
			}
			return null;
		}
		if (level() instanceof ServerLevel level) {
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(hauntedBy);
			if (player != null && player.isAlive()) {
				return player;
			}
		}
		fruits$setHauntedBy(null);
		return null;
	}

	@Override
	public void fruits$setHauntedBy(@Nullable UUID uuid) {
//		if ((Object) this instanceof Player player) {
//			Hooks.debugInChat(player, "setHauntedBy %s".formatted(uuid));
//		}
		hauntedBy = uuid;
	}

	@Override
	public boolean fruits$hasHauntedTrait(Trait trait) {
		Player player = fruits$getHauntedBy();
		if (player == null) {
			return false;
		}
		HauntingManager manager = FFPlayer.of(player).fruits$hauntingManager();
		return manager != null && manager.hasTrait(trait);
	}

	@Override
	public void fruits$setPinkGlowing() {
		pinkGlowing = 20;
	}

	@Override
	public boolean fruits$isPinkGlowing() {
		return pinkGlowing > 0;
	}

	@Inject(
			method = "actuallyHurt", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
	private void actuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
		if (Hooks.bee && !level().isClientSide && hauntedBy != null && damageAmount > 0 && damageSource.is(DamageTypes.IN_FIRE) &&
				fruits$getHauntedBy() instanceof ServerPlayer player) {
			HauntingManager hauntingManager = FFPlayer.of(player).fruits$hauntingManager();
			if (hauntingManager != null) {
				hauntingManager.hurtInFire(player);
			}
		}
	}

	@Inject(
			method = "hurt",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
	private void die(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!Hooks.bee || level().isClientSide) {
			return;
		}
		if (fruits$getHauntedBy() instanceof ServerPlayer player) {
			if (player.getHealth() > 2) {
				player.hurt(player.damageSources().genericKill(), player.getHealth() - 2);
			}
			HauntingManager hauntingManager = FFPlayer.of(player).fruits$hauntingManager();
			if (hauntingManager != null) {
				hauntingManager.getExorcised(player);
			}
		}
		if (source.getEntity() != null && source.getEntity().getType() == EntityType.RAVAGER && getType().is(EntityTypeTags.RAIDERS)) {
			Player player = ((FFLivingEntity) source.getEntity()).fruits$getHauntedBy();
			if (player != null) {
				HauntingManager hauntingManager = FFPlayer.of(player).fruits$hauntingManager();
				if (hauntingManager != null) {
					hauntingManager.onRavagerKill(player);
				}
			}
		}
	}

	@Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
	private void isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && fruits$isPinkGlowing()) {
			cir.setReturnValue(true);
		}
	}
}
