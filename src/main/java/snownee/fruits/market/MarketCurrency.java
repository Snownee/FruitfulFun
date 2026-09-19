package snownee.fruits.market;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FFCommonConfig;

public final class MarketCurrency {
	private MarketCurrency() {
	}

	public static int valueOf(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		return FFCommonConfig.currencyValues.getOrDefault(id, 0);
	}

	public static boolean isCurrency(ItemStack stack) {
		return valueOf(stack) > 0;
	}

	public static long unitPrice(ItemStack stack) {
		return stack.isEmpty() ? 0 : 20;
	}

	public static String format(long money) {
		return "%d.%02d%s".formatted(money / 100, money % 100, FFCommonConfig.currencyName);
	}
}
