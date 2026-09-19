package snownee.fruits.market;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;

public final class MarketPrices {
	private static final long BASE_PRICE = 20;

	private MarketPrices() {
	}

	public static ResourceKey<MarketPrice> createKey(Identifier id) {
		return ResourceKey.create(FFRegistries.MARKET_PRICE_KEY, id);
	}

	public static void bootstrap(BootstrapContext<MarketPrice> context) {
		context.register(
				createKey(FruitfulFun.id("fruits")),
				new MarketPrice(
						BASE_PRICE,
						context.lookup(Registries.ITEM).getOrThrow(ConventionalItemTags.FRUIT_FOODS)));
		for (Holder.Reference<FruitType> holder : FFRegistries.FRUIT_TYPE.listElements().toList()) {
			Item item = holder.value().fruit.get();
			Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
			long price = (holder.value().tier + 1L) * BASE_PRICE;
			context.register(
					createKey(FruitfulFun.id("fruit/" + itemId.getPath())),
					new MarketPrice(price, HolderSet.direct(BuiltInRegistries.ITEM.wrapAsHolder(item))));
		}
	}
}
