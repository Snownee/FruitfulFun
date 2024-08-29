package snownee.fruits.mixin.pomegranate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFDamageTypes;
import snownee.fruits.Hooks;

@Mixin(Explosion.class)
public class ExplosionMixin {
	@Shadow
	@Final
	private DamageSource damageSource;

	@Shadow
	public float radius;

	@WrapOperation(
			method = "explode",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean clampGrenadeExplosionDamage(Entity entity, DamageSource source, float f, Operation<Boolean> original) {
		if (FFDamageTypes.isGrenadeExplosion(source)) {
			boolean isPlayer = entity.getType() == EntityType.PLAYER;
			f = Math.min(f / (isPlayer ? 6F : 3F), isPlayer ? 1.5F : 3F);
		}
		return original.call(entity, source, f);
	}

	@WrapOperation(method = "explode", at = @At(value = "NEW", target = "Lnet/minecraft/world/phys/Vec3;", ordinal = 2))
	private Vec3 modifyDeltaMovement(double d, double e, double f, Operation<Vec3> original, @Local Entity entity) {
		if (FFDamageTypes.isGrenadeExplosion(damageSource)) {
			return Hooks.modifyExplosionDeltaMovement(entity, d, e, f, radius);
		}
		return original.call(d, e, f);
	}
}
