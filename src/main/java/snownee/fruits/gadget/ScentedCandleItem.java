package snownee.fruits.gadget;

import org.joml.Vector3f;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.item.ModBlockItem;
import snownee.kiwi.util.MathUtil;
import snownee.lychee.util.Color;

public class ScentedCandleItem extends ModBlockItem {
	public ScentedCandleItem(Block block, Item.Properties builder) {
		super(block, builder);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BuzzyPowerStorage.read(stack).map(BuzzyPowerStorage::hasLife).orElse(false);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BuzzyPowerStorage.read(stack).map($ -> {
			float red = $.red();
			float green = $.green();
			float blue = $.blue();
			if (red == 0 && green == 0 && blue == 0) {
				red = green = blue = 1;
			}
			Vector3f hsv = MathUtil.RGBtoHSV(new Color(red, green, blue, 1).getRGB());
			return Float.isNaN(hsv.x) ? 0xCCCCCC : Mth.hsvToRgb(hsv.x, hsv.y, 0.85f);
		}).orElse(0xCCCCCC);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return BuzzyPowerStorage.read(stack).map($ -> Math.round($.life() / $.maxLife() * 13f)).orElse(0);
	}
}
