package snownee.fruits.mixin.haunt;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements FFLivingEntity {
	@Unique
	@Nullable
	private UUID hauntedBy;

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
		fruits$getHauntedBy(); // remove invalid spectatedBy
		if (this instanceof FFPlayer player && player.fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Override
	@Nullable
	public Player fruits$getHauntedBy() {
		if (hauntedBy == null) {
			return null;
		}
		if (level() instanceof ServerLevel level && level.getEntity(hauntedBy) instanceof Player player && player.isAlive()) {
			return player;
		}
		hauntedBy = null;
		return null;
	}

	@Override
	public void fruits$setHauntedBy(@Nullable UUID uuid) {
		hauntedBy = uuid;
	}

	@Inject(
			method = "actuallyHurt", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
	private void actuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
		if (Hooks.bee && !level().isClientSide && hauntedBy != null && damageAmount > 0 && damageSource.is(DamageTypes.IN_FIRE) &&
				fruits$getHauntedBy() instanceof ServerPlayer player) {
			HauntingManager hauntingManager = ((FFPlayer) player).fruits$hauntingManager();
			if (hauntingManager != null) {
				hauntingManager.hurtInFire(player);
			}
		}
	}
}
