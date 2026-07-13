package snownee.fruits.gadget.crafter;

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
	default boolean canPlaceItem(int index, ItemStack stack) {
		return getTheItem().isEmpty();
	}
}
