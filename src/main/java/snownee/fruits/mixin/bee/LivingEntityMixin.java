package snownee.fruits.mixin.bee;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFLivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity);

	@Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
	private void addPoisonEffect(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
		if (Hooks.bee && !damageSource.isIndirect() && damageSource.getEntity() instanceof FFLivingEntity living &&
				!(damageSource.getEntity() instanceof Creeper) && living.fruits$hasHauntedTrait(Trait.WARRIOR)) {
			addEffect(new MobEffectInstance(MobEffects.POISON, 200), damageSource.getEntity());
		}
	}
}
