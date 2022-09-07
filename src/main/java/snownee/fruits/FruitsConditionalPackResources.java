package snownee.fruits;

import snownee.kiwi.loader.Platform;

public class FruitsConditionalPackResources extends ConditionalPackResources {

	boolean farmersdelight = Platform.isModLoaded("farmersdelight");

	public FruitsConditionalPackResources() {
		super(FruitsMod.ID);
	}

	@Override
	protected boolean test(String path) {
		if (FruitsConfig.villageAppleTreeWorldGen && "data/minecraft/structures/village/plains/town_centers/plains_meeting_point_3.nbt".equals(path)) {
			return true;
		}
		if (farmersdelight && path.startsWith("data/fruittrees/loot_tables/blocks/")) {
			return true;
		}
		return false;
	}

}
