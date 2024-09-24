package snownee.fruits.mixin.bee;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract boolean hasEffect(MobEffect effect);

	@Shadow
	@Nullable
	public abstract MobEffectInstance getEffect(MobEffect effect);

	@Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"))
	private void getDamageAfterMagicAbsorb(
			DamageSource damageSource,
			float damageAmount,
			CallbackInfoReturnable<Float> cir,
			@Local(argsOnly = true) LocalFloatRef damageAmountRef) {
		if (Hooks.bee && hasEffect(BeeModule.FRAGILITY.get())) {
			damageAmountRef.set(
					damageAmountRef.get() * (1.2F + Objects.requireNonNull(getEffect(BeeModule.FRAGILITY.get())).getAmplifier() * 0.2F));
		}
	}
}
