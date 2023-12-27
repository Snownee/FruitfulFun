package snownee.fruits;

import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class FruitsConditionalPackResources extends ConditionalPackResources {

	public FruitsConditionalPackResources(PackMetadataSection packInfo) {
		super(FruitfulFun.ID, packInfo);
	}

	@Override
	protected boolean test(String path) {
		if (FFCommonConfig.villageAppleTreeWorldGen && "data/minecraft/structures/village/plains/town_centers/plains_meeting_point_3.nbt".equals(path)) {
			return true;
		}
		if (Hooks.farmersdelight && path.startsWith("data/fruitfulfun/loot_tables/blocks/")) {
			return true;
		}
		return false;
	}

}
