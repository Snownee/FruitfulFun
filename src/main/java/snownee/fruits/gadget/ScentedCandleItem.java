package snownee.fruits.gadget;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.item.ModBlockItem;

public class ScentedCandleItem extends ModBlockItem {
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
}
