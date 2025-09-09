package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.BuzzyShieldItem;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	protected ItemStack useItem;

	@WrapOperation(
			method = "isBlocking",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getUseDuration(Lnet/minecraft/world/item/ItemStack;)I"))
	private int ignoreShieldUseDelay(Item item, ItemStack stack, Operation<Integer> original) {
		if (Hooks.gadget && item instanceof BuzzyShieldItem) {
			return 999999;
		}
		return original.call(item, stack);
	}

	@WrapOperation(
			method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
	private void recordDamage(
			LivingEntity instance,
			float damageAmount,
			Operation<Void> original,
			@Share("damage") LocalFloatRef damageRecord) {
		damageRecord.set(damageAmount);
		original.call(instance, damageAmount);
	}

	@Inject(
			method = "hurt", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 1))
	private void onBlocked(
			DamageSource source,
			float amount,
			CallbackInfoReturnable<Boolean> cir,
			@Share("damage") LocalFloatRef damageRecord,
			@Local(argsOnly = true) LocalFloatRef damageRef,
			@Local(index = 4) LocalBooleanRef blockedRef,
			@Local(index = 5) LocalFloatRef blockedDamageRef) {
		if (!Hooks.gadget || !(useItem.getItem() instanceof BuzzyShieldItem)) {
			return;
		}
		float newDamage = BuzzyShieldItem.onBlock((LivingEntity) (Object) this, source, damageRecord.get(), useItem);
		if (newDamage > 0) {
			blockedRef.set(false);
		}
		if (newDamage != damageRef.get()) {
			blockedDamageRef.set(damageRecord.get() - newDamage);
			damageRef.set(newDamage);
		}
	}

	@Inject(
			method = "hurt", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
			ordinal = 1))
	private void modifyInvulnerableTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.gadget && source.getEntity() instanceof LivingEntity entity) {
			ItemStack shield = BuzzyShieldItem.getItemInHand(entity);
			if (shield.isEmpty()) {
				return;
			}
			LivingEntity self = (LivingEntity) (Object) this;
			self.invulnerableTime = self.invulnerableTime / 2;
		}
	}
}
