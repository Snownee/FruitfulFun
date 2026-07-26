package snownee.fruits.mixin.scent;

import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.math.IntMath;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.FFRegistries;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.scent.ScentType;

@Mixin(value = LivingEntity.class, priority = 600)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

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
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/LivingEntity;activeEffects:Ljava/util/Map;",
					opcode = Opcodes.GETFIELD,
					ordinal = 0))
	private Map<Holder<MobEffect>, MobEffectInstance> canBeAffected(
			LivingEntity instance,
			Operation<Map<Holder<MobEffect>, MobEffectInstance>> original,
			@Local(argsOnly = true, name = "newEffect") MobEffectInstance newEffect) {
		Map<Holder<MobEffect>, MobEffectInstance> result = original.call(instance);
		if (Hooks.gadget && !Hooks.scentEffects.contains(newEffect.getEffect().value()) && !newEffect.isInfiniteDuration() &&
				!newEffect.getEffect().value().isInstantenous() && hasEffect(GadgetModule.WEAK_SCENT.holderOrThrow())) {
			newEffect.duration = IntMath.saturatedAdd(newEffect.getDuration(), newEffect.getDuration());
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
