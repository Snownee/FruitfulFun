package snownee.fruits.market;

import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import snownee.fruits.FFCommonConfig;

public final class MarketSales {
	private MarketSales() {
	}

	public static long currentDay(ServerLevel level) {
		return Math.floorDiv(level.getOverworldClockTime(), 24000L);
	}

	public static MarketSalesData data(ServerLevel level) {
		return level.getServer().overworld().getDataStorage().computeIfAbsent(MarketSalesData.TYPE);
	}

	public static void record(ServerLevel level, Map<Holder<Item>, Long> counts) {
		if (!FFCommonConfig.marketStats || counts.isEmpty()) {
			return;
		}
		long day = currentDay(level);
		MarketSalesData data = data(level);
		counts.forEach((item, count) -> data.add(day, item.unwrapKey().orElseThrow(), count));
		data.prune(day, FFCommonConfig.marketStatsDays);
	}
}
