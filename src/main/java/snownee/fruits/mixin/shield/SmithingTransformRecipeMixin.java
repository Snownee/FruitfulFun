package snownee.fruits.mixin.shield;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import snownee.fruits.gadget.GadgetModule;

@Mixin(SmithingTransformRecipe.class)
public class SmithingTransformRecipeMixin {
	@WrapOperation(
			method = "assemble", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;setTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void assemble(ItemStack itemStack, CompoundTag tag, Operation<Void> original) {
		original.call(itemStack, tag);
		if (GadgetModule.BUZZY_SHIELD.is(itemStack)) {
			itemStack.removeTagKey(ItemStack.TAG_DAMAGE);
			itemStack.removeTagKey("RepairCost");
			itemStack.removeTagKey(BlockItem.BLOCK_ENTITY_TAG);
			Map<Enchantment, Integer> enchantments = Maps.newLinkedHashMap(EnchantmentHelper.getEnchantments(itemStack));
			enchantments.keySet().removeIf(enchantment -> !enchantment.canEnchant(itemStack));
			EnchantmentHelper.setEnchantments(enchantments, itemStack);
		}
	}
}
