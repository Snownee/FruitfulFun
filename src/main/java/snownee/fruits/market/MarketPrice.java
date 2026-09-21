package snownee.fruits.market;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public record MarketPrice(long price, HolderSet<Item> items, double volatility, boolean demandSensitive) {
	public static final Codec<MarketPrice> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.LONG.fieldOf("price").forGetter(MarketPrice::price),
			RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(MarketPrice::items),
			Codec.DOUBLE.optionalFieldOf("volatility", 1.0).forGetter(MarketPrice::volatility),
			Codec.BOOL.optionalFieldOf("demandSensitive", true).forGetter(MarketPrice::demandSensitive)
	).apply(i, MarketPrice::new));

	public MarketPrice(long price, HolderSet<Item> items) {
		this(price, items, 1.0, true);
	}
}
