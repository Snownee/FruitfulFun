package snownee.fruits.mixin.scent;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.math.IntMath;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.FFRegistries;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.ScentType;

@Mixin(value = LivingEntity.class, priority = 600)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "randomTeleport", at = @At("HEAD"), cancellable = true)
	private void randomTeleport(double xx, double yy, double zz, boolean showParticles, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.gadget && GadgetModule.ENDER.get().isActiveAt(Objects.requireNonNull(level()).getChunkAt(blockPosition()))) {
			cir.setReturnValue(false);
		}
	}

	@WrapOperation(
			method = {"forceAddEffect", "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
	private boolean canBeAffected(
			LivingEntity entity,
			MobEffectInstance newEffect,
			Operation<Boolean> original,
			@Local(argsOnly = true) MobEffectInstance effect) {
		boolean result = original.call(entity, newEffect);
		if (result && Hooks.gadget && !Hooks.scentEffects.contains(effect.getEffect().value()) && !effect.isInfiniteDuration() &&
				!effect.getEffect().value().isInstantenous() && entity.hasEffect(GadgetModule.WEAK_SCENT.holderOrThrow())) {
			effect.duration = IntMath.saturatedAdd(effect.getDuration(), effect.getDuration());
		}
		return result;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		if (level().isClientSide() || !Hooks.gadget) {
			return;
		}
		LevelChunk chunk = level().getChunkAt(blockPosition());
		if (chunk.isEmpty()) {
			return;
		}
		for (ScentType type : FFRegistries.SCENT_TYPE) {
			if (type.isActiveAt(chunk)) {
				type.tick((LivingEntity) (Object) this, chunk);
			}
		}
	}
}
