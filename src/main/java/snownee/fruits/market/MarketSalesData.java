package snownee.fruits.market;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import snownee.fruits.FruitfulFun;

public class MarketSalesData extends SavedData {
	private static final Codec<MarketSalesData> CODEC = Entry.CODEC.listOf().fieldOf("sales").codec().xmap(
			list -> {
				MarketSalesData data = new MarketSalesData();
				for (Entry entry : list) {
					data.sales.computeIfAbsent(entry.day(), $ -> new TreeMap<>()).merge(entry.item(), entry.count(), Long::sum);
				}
				return data;
			},
			data -> {
				List<Entry> records = new ArrayList<>();
				data.sales.forEach((day, items) -> items.forEach((item, count) -> records.add(new Entry(day, item, count))));
				return records;
			});
	public static final SavedDataType<MarketSalesData> TYPE = new SavedDataType<>(
			FruitfulFun.id("market_sales"), MarketSalesData::new, CODEC, null);

	private final Map<Long, Map<Identifier, Long>> sales = new TreeMap<>();

	public void add(long day, Identifier item, long count) {
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

	public Map<Identifier, Long> itemsOf(long day) {
		Map<Identifier, Long> items = sales.get(day);
		return items == null ? Map.of() : items;
	}

	public Map<Identifier, Long> totals(long currentDay, int days) {
		Map<Identifier, Long> totals = new HashMap<>();
		sales.forEach((day, items) -> {
			if (day > currentDay - days) {
				items.forEach((item, count) -> totals.merge(item, count, Long::sum));
			}
		});
		return totals;
	}

	public record Entry(long day, Identifier item, long count) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.LONG.fieldOf("day").forGetter(Entry::day),
				Identifier.CODEC.fieldOf("item").forGetter(Entry::item),
				Codec.LONG.fieldOf("count").forGetter(Entry::count)
		).apply(instance, Entry::new));
	}
}
