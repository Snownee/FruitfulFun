package snownee.fruits.market;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.netty.buffer.ByteBuf;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import snownee.fruits.FFCommonConfig;

public final class MarketPricing {
	private static final double REVERSION = 0.7;
	private static final double NOISE = 0.35;
	private static final double MAX_AMPLITUDE = 0.8;

	private MarketPricing() {
	}

	public static boolean active() {
		return FFCommonConfig.marketPricingMode != FFCommonConfig.MarketPricingMode.Disabled && FFCommonConfig.marketStats;
	}

	public static double amplitude(MarketPrice price) {
		return Mth.clamp((double) FFCommonConfig.defaultVolatility * price.volatility(), 0, MAX_AMPLITUDE);
	}

	public static void advanceDay(ServerLevel level, long day) {
		if (!active()) {
			return;
		}
		MarketSalesData data = MarketSales.data(level);
		if (data.priceDay() >= day) {
			return;
		}
		data.setPriceDay(day);
		RandomSource random = level.getRandom();
		for (Item item : BuiltInRegistries.ITEM) {
			Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
			MarketPrice price = MarketCurrency.findPrice(holder, level.registryAccess());
			if (price == null || price.price() <= 0) {
				continue;
			}
			ResourceKey<Item> key = holder.unwrapKey().orElseThrow();
			double amplitude = amplitude(price);
			if (amplitude <= 0) {
				data.resetWalk(key);
				continue;
			}
			double current = data.walk(key);
			double next = 1.0 + (current - 1.0) * REVERSION + random.nextGaussian() * amplitude * NOISE;
			next = Mth.clamp(next, 1.0 - amplitude, 1.0 + amplitude);
			data.setWalk(key, next);
		}
	}

	public static void reanchor(ServerLevel level, long day) {
		if (!active()) {
			return;
		}
		MarketSalesData data = MarketSales.data(level);
		if (data.priceDay() > day) {
			data.setPriceDay(day);
		}
	}

	public static long unitPrice(Item item, ServerLevel level) {
		MarketPrice price = MarketCurrency.findPrice(BuiltInRegistries.ITEM.wrapAsHolder(item), level.registryAccess());
		if (price == null || price.price() <= 0) {
			return 0;
		}
		return price(price, item, level);
	}

	public static long price(MarketPrice price, Item item, ServerLevel level) {
		if (!active()) {
			return price.price();
		}
		MarketSalesData data = MarketSales.data(level);
		ResourceKey<Item> key = item.builtInRegistryHolder().key();
		long day = MarketSales.currentDay(level);
		double walk = amplitude(price) > 0 ? data.walk(key) : 1.0;
		return Math.max(1, Math.round(price.price() * factor(price, data, key, walk, day)));
	}

	public static float deltaPercent(MarketPrice price, Item item, ServerLevel level) {
		if (!active()) {
			return 0;
		}
		MarketSalesData data = MarketSales.data(level);
		ResourceKey<Item> key = item.builtInRegistryHolder().key();
		long day = MarketSales.currentDay(level);
		boolean random = amplitude(price) > 0;
		double walk = random ? data.walk(key) : 1.0;
		double previousWalk = random ? data.previousWalk(key) : 1.0;
		double previous = factor(price, data, key, previousWalk, day - 1);
		if (previous <= 0) {
			return 0;
		}
		double current = factor(price, data, key, walk, day);
		return (float) ((current - previous) / previous * 100.0);
	}

	private static double factor(MarketPrice price, MarketSalesData data, ResourceKey<Item> key, double walk, long day) {
		double demand = price.demandSensitive() ? demandFactor(data, key, day) : 1.0;
		return Mth.clamp(
				walk * demand,
				FFCommonConfig.minMarketMultiplier,
				FFCommonConfig.maxMarketMultiplier);
	}

	private static double demandFactor(MarketSalesData data, ResourceKey<Item> key, long day) {
		return switch (FFCommonConfig.marketPricingMode) {
			case Disabled -> 1.0;
			case SoftResponse -> {
				double demand = data.decayedSales(key, day, FFCommonConfig.demandHalfLifeDays);
				yield 1.0 + FFCommonConfig.softResponseStrength * Math.log1p(demand / FFCommonConfig.softDemandScale);
			}
			case Incremental -> {
				double shortRate = decayedRate(data, key, day, FFCommonConfig.incrementalShortHalfLifeDays);
				double longRate = decayedRate(data, key, day, FFCommonConfig.incrementalLongHalfLifeDays);
				double excess = Math.max(0.0, shortRate - longRate);
				yield 1.0 + (FFCommonConfig.maxDemandFactor - 1.0) * excess / (excess + FFCommonConfig.incrementalScaleK);
			}
		};
	}

	private static double decayedRate(MarketSalesData data, ResourceKey<Item> key, long day, double halfLifeDays) {
		double window = 1.0 / (1.0 - Math.pow(0.5, 1.0 / halfLifeDays));
		return data.decayedSales(key, day, halfLifeDays) / window;
	}

	public static List<PriceEntry> priceEntries(ServerLevel level, Collection<Holder<Item>> items) {
		if (!active()) {
			return List.of();
		}
		List<PriceEntry> entries = new ArrayList<>(items.size());
		for (Holder<Item> holder : items) {
			MarketPrice price = MarketCurrency.findPrice(holder, level.registryAccess());
			if (price == null || price.price() <= 0) {
				continue;
			}
			Item item = holder.value();
			entries.add(new PriceEntry(
					holder.unwrapKey().orElseThrow(),
					price(price, item, level),
					deltaPercent(price, item, level)));
		}
		return entries;
	}

	public record PriceEntry(ResourceKey<Item> item, long price, float delta) {
		public static final StreamCodec<ByteBuf, PriceEntry> STREAM_CODEC = StreamCodec.composite(
				ResourceKey.streamCodec(Registries.ITEM), PriceEntry::item,
				ByteBufCodecs.LONG, PriceEntry::price,
				ByteBufCodecs.FLOAT, PriceEntry::delta,
				PriceEntry::new);
	}
}
