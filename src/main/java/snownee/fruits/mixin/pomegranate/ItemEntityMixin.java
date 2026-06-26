package snownee.fruits.mixin.pomegranate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import snownee.fruits.FFDamageTypes;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
	private void cancelGrenadeExplosionDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
		if (FFDamageTypes.isGrenadeExplosion(source)) {
			cir.setReturnValue(false);
		}
	}
}
