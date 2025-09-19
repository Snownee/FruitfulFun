package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import snownee.fruits.gadget.BuzzyShieldItem;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
	@WrapOperation(
			method = "renderByItem", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private boolean renderByItem(ItemStack itemStack, Item item, Operation<Boolean> original) {
		if (itemStack.getItem() instanceof BuzzyShieldItem && item instanceof ShieldItem) {
			return true;
		}
		return original.call(itemStack, item);
	}
}
