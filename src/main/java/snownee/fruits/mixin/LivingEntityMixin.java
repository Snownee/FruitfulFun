package snownee.fruits.mixin;

import org.jspecify.annotations.Nullable;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.fruits.CoreModule;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	@Nullable
	public abstract MobEffectInstance getEffect(MobEffect effect);

	@Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"))
	private void getDamageAfterMagicAbsorb(
			DamageSource damageSource,
			float damageAmount,
			CallbackInfoReturnable<Float> cir,
			@Local(argsOnly = true) LocalFloatRef damageAmountRef) {
		MobEffectInstance effect = getEffect(CoreModule.FRAGILITY.get());
		if (effect != null) {
			damageAmountRef.set(damageAmountRef.get() * (1.2F + effect.getAmplifier() * 0.2F));
		}
	}

	@Inject(method = "eat", at = @At("HEAD"))
	private void eat(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> cir) {
		if (food.isEdible() && food.is(CoreModule.CITRUS_FRUITS)) {
			LivingEntity self = (LivingEntity) (Object) this;
			self.extinguishFire();
		}
	}
}
