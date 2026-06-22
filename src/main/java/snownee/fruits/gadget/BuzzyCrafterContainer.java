package snownee.fruits.gadget;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ticks.ContainerSingleItem;

// method "isEmpty" conflict with BeehiveBlockEntity
// do not call Container#isEmpty directly because it will mess up with beehive in dev environment
public interface BuzzyCrafterContainer extends ContainerSingleItem {
	@Override
	default int getMaxStackSize() {
		return 1;
	}

	@Override
	default ItemStack removeItem(int pSlot, int pAmount) {
		if (getTheItem().isEmpty() || pAmount <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack item = getTheItem();
		ItemStack itemstack = item.copyWithCount(pAmount);
		setItem(0, item.copyWithCount(item.getCount() - pAmount)); // play the removing sound if possible
		return itemstack;
	}

	@Override
	default boolean canPlaceItem(int index, ItemStack stack) {
		return getTheItem().isEmpty();
	}
}
