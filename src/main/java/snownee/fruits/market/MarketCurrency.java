package snownee.fruits.market;

import org.jspecify.annotations.Nullable;

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
		MarketPrice price = findPrice(stack, registries);
		return price == null ? 0 : price.price();
	}

	public static @Nullable MarketPrice findPrice(ItemStack stack, HolderLookup.Provider registries) {
		if (stack.isEmpty()) {
			return null;
		}
		return findPrice(stack.typeHolder(), registries);
	}

	public static @Nullable MarketPrice findPrice(Holder<Item> item, HolderLookup.Provider registries) {
		MarketPrice tagPrice = null;
		for (Holder.Reference<MarketPrice> holder : registries.lookupOrThrow(FFRegistries.MARKET_PRICE_KEY).listElements().toList()) {
			MarketPrice price = holder.value();
			if (!price.items().contains(item)) {
				continue;
			}
			if (price.items().unwrap().left().isEmpty()) {
				return price;
			}
			if (tagPrice == null) {
				tagPrice = price;
			}
		}
		return tagPrice;
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
