package snownee.fruits.gadget;

import java.util.List;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.gadget.crafter.BuzzyCrafterBlockEntity;
import snownee.kiwi.item.ItemCategoryFiller;

public interface BuzzyItemCategoryFiller extends ItemCategoryFiller {
	@Override
	default void fillItemCategory(CreativeModeTab creativeModeTab, FeatureFlagSet featureFlagSet, boolean b, List<ItemStack> list) {
		ItemLike item = (ItemLike) this;
		ItemStack itemStack = item.asItem().getDefaultInstance();
		list.add(itemStack);
		BuzzyPowerStorage storage = BuzzyCrafterBlockEntity.getPowerStorage(itemStack);
		if (storage != null) {
			itemStack = itemStack.copy();
			storage.addLife(storage.maxLife());
			itemStack.set(GadgetModule.BUZZY_POWER_STORAGE.get(), storage);
			list.add(itemStack);
		}
	}
}
