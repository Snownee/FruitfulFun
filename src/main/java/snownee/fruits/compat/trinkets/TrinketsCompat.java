package snownee.fruits.compat.trinkets;

import org.jspecify.annotations.Nullable;

import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.cherry.item.FlowerCrownItem;

public class TrinketsCompat {
	public static void init() {}

	public static @Nullable FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		return TrinketsApi.getAttachment(entity)
				.findFirst(itemStack -> itemStack.getItem() instanceof FlowerCrownItem)
				.map(slot -> (FlowerCrownItem) slot.get().getItem())
				.orElse(null);
	}
}
