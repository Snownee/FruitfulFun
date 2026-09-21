package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.shield.BuzzyShieldItem;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@WrapOperation(
			method = "getItemBlockingWith",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/Item;getUseDuration(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int ignoreShieldUseDelay(Item item, ItemStack itemStack, LivingEntity user, Operation<Integer> original) {
		if (Hooks.gadget && item instanceof BuzzyShieldItem) {
			return 999999;
		}
		return original.call(item, itemStack, user);
	}

	@WrapOperation(
			method = "hurtServer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
	private float buzzyShieldBlock(
			LivingEntity self,
			ServerLevel level,
			DamageSource source,
			float damage,
			Operation<Float> original) {
		float blocked = original.call(self, level, source, damage);
		if (!Hooks.gadget || blocked <= 0) {
			return blocked;
		}
		ItemStack shield = self.getItemBlockingWith();
		if (shield == null || !(shield.getItem() instanceof BuzzyShieldItem)) {
			return blocked;
		}
		float remaining = BuzzyShieldItem.onBlock(self, source, damage, shield);
		return damage - remaining;
	}

	@Inject(
			method = "hurtServer", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
			ordinal = 1))
	private void modifyInvulnerableTime(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
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
