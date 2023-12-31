/*
package snownee.fruits;

import java.io.InputStream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;

public class FFConditionalPackResources extends ConditionalPackResources {
	public FFConditionalPackResources() {
		super(FruitfulFun.ID, FruitfulFun.NAME, true);
	}

//	public FruitsConditionalPackResources(PackMetadataSection packInfo) {
//		super(FruitfulFun.ID, packInfo);
//	}

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
*/
