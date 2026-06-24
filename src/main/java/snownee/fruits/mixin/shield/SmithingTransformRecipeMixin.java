package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import snownee.fruits.gadget.GadgetModule;

@Mixin(SmithingTransformRecipe.class)
public class SmithingTransformRecipeMixin {
	@WrapOperation(
			method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;)Lnet/minecraft/world/item/ItemStack;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/crafting/TransmuteRecipe;createWithOriginalComponents(Lnet/minecraft/world/item/ItemStackTemplate;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack assemble(ItemStackTemplate target, ItemStack input, Operation<ItemStack> original) {
		ItemStack itemStack = original.call(target, input);
		if (GadgetModule.BUZZY_SHIELD.is(itemStack)) {
			itemStack.remove(DataComponents.DAMAGE);
			itemStack.remove(DataComponents.MAX_DAMAGE);
			itemStack.remove(DataComponents.BASE_COLOR);
			itemStack.remove(DataComponents.REPAIR_COST);
			itemStack.remove(DataComponents.REPAIRABLE);
			itemStack.remove(DataComponents.BANNER_PATTERNS);
			ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(itemStack.getEnchantments());
			enchantments.removeIf(enchantment -> !enchantment.value().canEnchant(itemStack));
			EnchantmentHelper.setEnchantments(itemStack, enchantments.toImmutable());
		}
		return itemStack;
	}
}
