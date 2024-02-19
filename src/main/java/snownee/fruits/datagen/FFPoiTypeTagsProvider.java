package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;

public class FFPoiTypeTagsProvider extends FabricTagProvider<PoiType> {
	public FFPoiTypeTagsProvider(
			FabricDataOutput output,
			ResourceKey<? extends Registry<PoiType>> registryKey,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registryKey, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		FabricTagProvider<PoiType>.FabricTagBuilder builder = getOrCreateTagBuilder(CoreModule.POI_TYPE);
		for (ResourceLocation id : FFRegistries.FRUIT_TYPE.keySet()) {
			builder.add(id);
		}
	}
}
