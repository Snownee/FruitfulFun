package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.BindingCurseEnchantment;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.BuzzyShieldItem;

@Mixin(BindingCurseEnchantment.class)
public class BindingCurseEnchantmentMixin {
	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.gadget && stack.getItem() instanceof BuzzyShieldItem) {
			cir.setReturnValue(false);
		}
	}
}
