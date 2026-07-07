package snownee.fruits.datagen;

import static snownee.kiwi.AbstractModule.itemTag;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class FFLanguage extends KiwiLanguageProvider {
	public FFLanguage(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(packOutput, registryLookup);
	}

	@Override
	protected void preGenerate(HolderLookup.Provider lookup, TreeMap<String, String> translationEntries) {
		generateGameObjectsEntries(lookup, translationEntries);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
		builder.add(FFItemTags.CITRUS_LOGS, "Citrus Logs");
		builder.add(FFItemTags.REDLOVE_LOGS, "Redlove Logs");
		builder.add(FFItemTags.TULIPS, "Tulips");
		builder.add(FFItemTags.GADGET_TOKEN, "Gadget Token");
		builder.add(itemTag("c:flowers/lavender"), "Lavender");
		builder.add(itemTag("c:chocolatebar"), "Chocolate Bar");
		List<String> crops = List.of(
				"tangerine",
				"grapefruit",
				"lemon",
				"pomelo",
				"apple",
				"cherry",
				"citron",
				"orange",
				"lime",
				"pomegranate",
				"citrus");
		for (String crop : crops) {
			builder.add(itemTag("c:crops/" + crop), StringUtils.capitalize(crop));
		}
	}
}
