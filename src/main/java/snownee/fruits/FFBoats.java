package snownee.fruits;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import snownee.fruits.cherry.CherryModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("boats")
public class FFBoats extends AbstractModule {
	public static final KiwiGO<EntityType<Boat>> CITRUS_BOAT = boat(CoreModule.CITRUS_BOAT);
	public static final KiwiGO<EntityType<ChestBoat>> CITRUS_CHEST_BOAT = chestBoat(CoreModule.CITRUS_CHEST_BOAT);
	public static final KiwiGO<EntityType<Boat>> REDLOVE_BOAT = boat(CherryModule.REDLOVE_BOAT);
	public static final KiwiGO<EntityType<ChestBoat>> REDLOVE_CHEST_BOAT = chestBoat(CherryModule.REDLOVE_CHEST_BOAT);

	public static KiwiGO<EntityType<Boat>> boat(ItemObject<?> drop) {
		return entity($ -> EntityType.Builder.of(EntityType.boatFactory(drop::getOrCreate), MobCategory.MISC)
				.noLootTable()
				.sized(1.375F, 0.5625F)
				.eyeHeight(0.5625F)
				.clientTrackingRange(10)
				.build($));
	}

	public static KiwiGO<EntityType<ChestBoat>> chestBoat(ItemObject<?> drop) {
		return entity($ -> EntityType.Builder.of(EntityType.chestBoatFactory(drop::getOrCreate), MobCategory.MISC)
				.noLootTable()
				.sized(1.375F, 0.5625F)
				.eyeHeight(0.5625F)
				.clientTrackingRange(10)
				.build($));
	}
}
