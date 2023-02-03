package snownee.fruits;

import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class FruitsConditionalPackResources extends ConditionalPackResources {

	public FruitsConditionalPackResources(PackMetadataSection packInfo) {
		super(FruitsMod.ID, packInfo);
	}

	@Override
	protected boolean test(String path) {
		if (FruitsConfig.villageAppleTreeWorldGen && "data/minecraft/structures/village/plains/town_centers/plains_meeting_point_3.nbt".equals(path)) {
			return true;
		}
		if (Hooks.farmersdelight && path.startsWith("data/fruittrees/loot_tables/blocks/")) {
			return true;
		}
		return false;
	}

}
