package snownee.fruits.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public interface PreventUpdateAnimation extends IForgeItem {
	@Override
	default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
}
