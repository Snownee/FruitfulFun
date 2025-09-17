package snownee.fruits.gadget;

import java.util.List;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.item.ModBlockItem;

public class ScentedCandleItem extends ModBlockItem implements BuzzyItemCategoryFiller {
	public ScentedCandleItem(Block block, Item.Properties builder) {
		super(block, builder);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BuzzyPowerStorage.isBarVisible(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BuzzyPowerStorage.getBarColor(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return BuzzyPowerStorage.getBarWidth(stack);
	}

	public static BuzzyPowerStorage getPowerStorage(ItemStack itemStack) {
		return BuzzyPowerStorage.read(itemStack).orElseGet(() -> new BuzzyPowerStorage(50000f));
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, FeatureFlagSet flags, boolean hasPermissions, List<ItemStack> items) {
		BuzzyItemCategoryFiller.super.fillItemCategory(tab, flags, hasPermissions, items);
	}
}
