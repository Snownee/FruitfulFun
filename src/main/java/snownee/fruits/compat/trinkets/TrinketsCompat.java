package snownee.fruits.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.item.FlowerCrownItem;

public class TrinketsCompat {
	public static void init() {
		FlowerCrownRenderer renderer = new FlowerCrownRenderer();
		TrinketRendererRegistry.registerRenderer(CherryModule.CHERRY_CROWN.getOrCreate(), renderer);
		TrinketRendererRegistry.registerRenderer(CherryModule.REDLOVE_CROWN.getOrCreate(), renderer);
	}

	public static FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		return TrinketsApi.getTrinketComponent(entity)
				.flatMap($ -> $.getEquipped($$ -> $$.getItem().getClass() == FlowerCrownItem.class).stream().findFirst())
				.map($ -> ((FlowerCrownItem) $.getB().getItem()))
				.orElse(null);
	}
}
