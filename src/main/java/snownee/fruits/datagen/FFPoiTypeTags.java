package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.gadget.GadgetModule;

public class FFPoiTypeTags extends FabricTagsProvider<PoiType> {
	public FFPoiTypeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.POINT_OF_INTEREST_TYPE, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		var builder = getOrCreateRawBuilder(CoreModule.POI_TYPE);
		for (Identifier id : FFRegistries.FRUIT_TYPE.keySet()) {
			builder.addElement(id);
		}
		getOrCreateRawBuilder(PoiTypeTags.BEE_HOME).addOptionalElement(GadgetModule.BUZZY_CRAFTER_POI.key());
	}
}
