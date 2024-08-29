package snownee.fruits.mixin.pomegranate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import snownee.fruits.FFDamageTypes;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void cancelGrenadeExplosionDamage(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir) {
		if (FFDamageTypes.isGrenadeExplosion(source)) {
			cir.setReturnValue(false);
		}
	}
}
