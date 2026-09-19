package snownee.fruits.market;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFRegistries;

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

	public static long unitPrice(ItemStack stack, HolderLookup.Provider registries) {
		if (stack.isEmpty()) {
			return 0;
		}
		Holder<Item> item = stack.typeHolder();
		MarketPrice tagPrice = null;
		for (Holder.Reference<MarketPrice> holder : registries.lookupOrThrow(FFRegistries.MARKET_PRICE_KEY).listElements().toList()) {
			MarketPrice price = holder.value();
			if (!price.items().contains(item)) {
				continue;
			}
			if (price.items().unwrap().left().isEmpty()) {
				return price.price();
			}
			if (tagPrice == null) {
				tagPrice = price;
			}
		}
		return tagPrice == null ? 0 : tagPrice.price();
	}

	public static String format(long money) {
		return "%d.%02d%s".formatted(money / 100, money % 100, FFCommonConfig.currencyName);
	}

	public static String formatCompact(long money) {
		if (money % 100 == 0) {
			return "%d%s".formatted(money / 100, FFCommonConfig.currencyName);
		}
		return format(money);
	}
}
