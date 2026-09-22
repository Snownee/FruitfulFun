package snownee.fruits;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import snownee.fruits.bee.BeeVariant;
import snownee.fruits.market.MarketPrice;

@EventBusSubscriber(modid = FruitfulFun.ID)
public final class FFNeoDatapackRegistries {

	@SubscribeEvent
	public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(FFRegistries.BEE_VARIANT_KEY, BeeVariant.DIRECT_CODEC, BeeVariant.NETWORK_CODEC);
		event.dataPackRegistry(FFRegistries.MARKET_PRICE_KEY, MarketPrice.CODEC, MarketPrice.CODEC);
	}
}
