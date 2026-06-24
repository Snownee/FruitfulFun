package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import snownee.fruits.CoreModule;

public class FFInstrumentTagsProvider extends FabricTagsProvider<Instrument> {
	public FFInstrumentTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.INSTRUMENT, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		builder(CoreModule.HORN_HARVESTING_INSTRUMENT).add(Instruments.SING_GOAT_HORN);
	}
}
