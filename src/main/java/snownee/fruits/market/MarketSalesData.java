package snownee.fruits.market;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import snownee.fruits.FruitfulFun;

public class MarketSalesData extends SavedData {
	private static final Codec<MarketSalesData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Entry.CODEC.listOf().fieldOf("sales").forGetter(MarketSalesData::entries),
			Codec.unboundedMap(ResourceKey.codec(Registries.ITEM), Codec.DOUBLE).optionalFieldOf("walk", Map.of())
					.forGetter(data -> Map.copyOf(data.walks)),
			Codec.unboundedMap(ResourceKey.codec(Registries.ITEM), Codec.DOUBLE).optionalFieldOf("prevWalk", Map.of())
					.forGetter(data -> Map.copyOf(data.previousWalks)),
			Codec.LONG.optionalFieldOf("priceDay", Long.MIN_VALUE).forGetter(MarketSalesData::priceDay)
	).apply(instance, MarketSalesData::from));
	public static final SavedDataType<MarketSalesData> TYPE = new SavedDataType<>(
			FruitfulFun.id("market_sales"), MarketSalesData::new, CODEC, null);

	private final Long2ObjectMap<Map<ResourceKey<Item>, Long>> sales = new Long2ObjectRBTreeMap<>();
	private final Map<ResourceKey<Item>, Double> walks = new HashMap<>();
	private final Map<ResourceKey<Item>, Double> previousWalks = new HashMap<>();
	private long priceDay = Long.MIN_VALUE;

	private static MarketSalesData from(
			List<Entry> entries,
			Map<ResourceKey<Item>, Double> walks,
			Map<ResourceKey<Item>, Double> previousWalks,
			long priceDay) {
		MarketSalesData data = new MarketSalesData();
		for (Entry entry : entries) {
			data.sales.computeIfAbsent(entry.day(), $ -> new TreeMap<>()).merge(entry.item(), entry.count(), Long::sum);
		}
		data.walks.putAll(walks);
		data.previousWalks.putAll(previousWalks);
		data.priceDay = priceDay;
		return data;
	}

	private List<Entry> entries() {
		List<Entry> records = new ArrayList<>();
		sales.forEach((day, items) -> items.forEach((item, count) -> records.add(new Entry(day, item, count))));
		return records;
	}

	public void add(long day, ResourceKey<Item> item, long count) {
		if (count <= 0) {
			return;
		}
		sales.computeIfAbsent(day, $ -> new TreeMap<>()).merge(item, count, Long::sum);
		setDirty();
	}

	public void prune(long currentDay, int days) {
		if (sales.keySet().removeIf(day -> day <= currentDay - days)) {
			setDirty();
		}
	}

	public Map<ResourceKey<Item>, Long> itemsOf(long day) {
		return sales.getOrDefault(day, Map.of());
	}

	public Map<ResourceKey<Item>, Long> totals(long currentDay, int days) {
		Map<ResourceKey<Item>, Long> totals = new HashMap<>();
		sales.forEach((day, items) -> {
			if (day > currentDay - days) {
				items.forEach((item, count) -> totals.merge(item, count, Long::sum));
			}
		});
		return totals;
	}

	public double decayedSales(ResourceKey<Item> item, long currentDay, double halfLifeDays) {
		double total = 0;
		for (var entry : sales.long2ObjectEntrySet()) {
			long age = currentDay - entry.getLongKey();
			if (age < 0) {
				continue;
			}
			Long count = entry.getValue().get(item);
			if (count == null) {
				continue;
			}
			total += count * Math.pow(0.5, age / halfLifeDays);
		}
		return total;
	}

	public double walk(ResourceKey<Item> item) {
		return walks.getOrDefault(item, 1.0);
	}

	public double previousWalk(ResourceKey<Item> item) {
		return previousWalks.getOrDefault(item, 1.0);
	}

	public void setWalk(ResourceKey<Item> item, double value) {
		previousWalks.put(item, walk(item));
		walks.put(item, value);
		setDirty();
	}

	public void resetWalk(ResourceKey<Item> item) {
		if (walks.remove(item) != null) {
			setDirty();
		}
	}

	public long priceDay() {
		return priceDay;
	}

	public void setPriceDay(long day) {
		priceDay = day;
		setDirty();
	}

	public record Entry(long day, ResourceKey<Item> item, long count) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.LONG.fieldOf("day").forGetter(Entry::day),
				ResourceKey.codec(Registries.ITEM).fieldOf("item").forGetter(Entry::item),
				Codec.LONG.fieldOf("count").forGetter(Entry::count)
		).apply(instance, Entry::new));
	}
}
