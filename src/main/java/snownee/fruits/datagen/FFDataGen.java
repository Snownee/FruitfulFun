package snownee.fruits.datagen;

import com.klikli_dev.modonomicon.api.datagen.AddToModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.FabricBookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeVariants;
import snownee.fruits.cherry.datagen.CherryBlockLoot;
import snownee.fruits.compat.farmersdelight.FarmersDelightBlockLoot;
import snownee.fruits.datagen.guide.FruitfulFunBook;
import snownee.fruits.datagen.guide.RitualMultiblockProvider;
import snownee.fruits.food.datagen.FoodBlockLoot;
import snownee.fruits.gadget.datagen.GadgetBlockLoot;
import snownee.fruits.market.datagen.MarketBlockLoot;
import snownee.fruits.minigame.datagen.MinigameBlockLoot;
import snownee.fruits.minigame.datagen.MinigameLootTables;
import snownee.fruits.pomegranate.datagen.PomegranateBlockLoot;

public class FFDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(CoreBlockLoot::new);
		pack.addProvider(FFAdvancements::new);
		FabricTagsProvider.BlockTagsProvider ffBlockTagsProvider = pack.addProvider(FFBlockTags::new);
		pack.addProvider((output, registriesFuture) -> new FFItemTags(output, registriesFuture, ffBlockTagsProvider));
		pack.addProvider(FFPoiTypeTags::new);
		pack.addProvider(FFDamageTypeTags::new);
		pack.addProvider(FFInstrumentTags::new);
		pack.addProvider(FFEntityTypeTags::new);
		FabricTagsProvider.BlockTagsProvider seasonalBlockTagsProvider = pack.addProvider(SeasonalBlockTags::new);
		pack.addProvider((output, registriesFuture) -> new SeasonalItemTags(output, registriesFuture, seasonalBlockTagsProvider));
		pack.addProvider(FFBiomeTags::new);
		pack.addProvider(FFBannerPatternTags::new);
		pack.addProvider(FFModels::new);
		pack.addProvider(FFRecipes::new);
		pack.addProvider(FFDynamicRegistryProvider::new);
		pack.addProvider(FFLanguage::new);
//		pack.addProvider(($, _) -> new FFEquipmentAssetProvider($));
		pack.addProvider(CherryBlockLoot::new);
		pack.addProvider(PomegranateBlockLoot::new);
		pack.addProvider(MinigameBlockLoot::new);
		pack.addProvider(MinigameLootTables::new);
		pack.addProvider((output, registries) -> new RitualMultiblockProvider(output));
		LanguageProviderCache zhCnLang = new LanguageProviderCache("zh_cn");
		pack.addProvider(FabricBookProvider.of(
				FruitfulFun.ID,
				zhCnLang,
				new ResearchCache(),
				new FruitfulFunBook()));
		pack.addProvider((FabricPackOutput output) -> new AddToModonomiconLanguageProvider(output, "fruitfulfunguide", "zh_cn", zhCnLang));
		pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("food"));
		pack.addProvider(FoodBlockLoot::new);
		pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("gadget"));
		pack.addProvider(GadgetBlockLoot::new);
		pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("market"));
		pack.addProvider(MarketBlockLoot::new);
		if (Hooks.farmersdelight) {
			pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("farmersdelight"));
			pack.addProvider(FarmersDelightBlockLoot::new);
			pack.addProvider(FFFDModels::new);
			pack.addProvider(FFFDCuttingRecipes::new);
			pack.addProvider(FFFDCookingRecipes::new);
		}
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, FFDynamicRegistryProvider::configureConfiguredFeatures);
		registryBuilder.add(Registries.PLACED_FEATURE, FFDynamicRegistryProvider::configurePlacedFeatures);
		registryBuilder.add(FFRegistries.BEE_VARIANT_KEY, BeeVariants::bootstrap);
	}
}
