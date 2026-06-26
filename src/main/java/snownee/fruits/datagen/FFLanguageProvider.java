package snownee.fruits.datagen;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class FFLanguageProvider extends KiwiLanguageProvider {
	public FFLanguageProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(packOutput, registryLookup);
	}

	@Override
	protected void preGenerate(HolderLookup.Provider lookup, TreeMap<String, String> translationEntries) {
		generateGameObjectsEntries(lookup, translationEntries);
	}
}
