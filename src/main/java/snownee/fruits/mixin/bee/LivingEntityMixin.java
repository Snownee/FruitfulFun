package snownee.fruits.mixin.bee;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFLivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract boolean hasEffect(MobEffect effect);

	@Shadow
	@Nullable
	public abstract MobEffectInstance getEffect(MobEffect effect);

	@Shadow
	public abstract boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity);

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

	@Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
	private void addPoisonEffect(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
		if (Hooks.bee && !damageSource.isIndirect() && damageSource.getEntity() instanceof FFLivingEntity living &&
				!(damageSource.getEntity() instanceof Creeper) && living.fruit$hasHauntedTrait(Trait.WARRIOR)) {
			addEffect(new MobEffectInstance(MobEffects.POISON, 200), damageSource.getEntity());
		}
	}
}
