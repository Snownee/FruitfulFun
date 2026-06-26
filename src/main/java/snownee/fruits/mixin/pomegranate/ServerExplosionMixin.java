package snownee.fruits.mixin.pomegranate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFDamageTypes;
import snownee.fruits.Hooks;

@Mixin(ServerExplosion.class)
public class ServerExplosionMixin {
	@Shadow
	@Final
	private DamageSource damageSource;

	@Shadow
	@Final
	private float radius;

	@WrapOperation(
			method = "hurtEntities",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean clampGrenadeExplosionDamage(
			Entity entity,
			ServerLevel level,
			DamageSource damageSource,
			float f,
			Operation<Boolean> original) {
		if (FFDamageTypes.isGrenadeExplosion(damageSource)) {
			boolean isPlayer = entity.getType() == EntityType.PLAYER;
			f = Math.min(f / (isPlayer ? 6F : 3F), isPlayer ? 1.5F : 3F);
		}
		return original.call(entity, level, damageSource, f);
	}

	@WrapOperation(
			method = "hurtEntities",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(Lnet/minecraft/world/phys/Vec3;)V"))
	private void modifyDeltaMovement(Entity entity, Vec3 impulse, Operation<Void> original) {
		if (FFDamageTypes.isGrenadeExplosion(damageSource)) {
			impulse = Hooks.modifyExplosionDeltaMovement(entity, impulse, radius);
		}
		original.call(entity, impulse);
	}
}
