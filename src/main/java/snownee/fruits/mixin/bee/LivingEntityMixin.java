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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;

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

//	@Inject(
//			method = "die", at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"), cancellable = true)
//	private void die(DamageSource damageSource, CallbackInfo ci) {
//		if (!Hooks.bee || getType() != EntityType.BEE || !damageSource.is(DamageTypes.DRAGON_BREATH)) {
//			return;
//		}
//		Bee bee = (Bee) (Object) this;
//		bee.heal(bee.getMaxHealth());
//		bee.stopBeingAngry();
//		bee.setHasNectar(false);
//		BeeAttributes attributes = BeeAttributes.of(bee);
//		attributes.dropSaddle(bee);
//		attributes.getGenes().addExtraTrait(Trait.GHOST);
//		attributes.updateTraits(bee);
//		ci.cancel();
//	}
}
