package snownee.fruits.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.registries.Registries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.cherry.datagen.CherryBlockLoot;
import snownee.fruits.compat.farmersdelight.FarmersDelightBlockLoot;
import snownee.fruits.food.datagen.FoodBlockLoot;
import snownee.fruits.gadget.datagen.GadgetBlockLoot;
import snownee.fruits.pomegranate.datagen.PomegranateBlockLoot;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class FFDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(CoreBlockLoot::new);
		pack.addProvider(FFAdvancements::new);
		FabricTagsProvider.BlockTagsProvider ffBlockTagsProvider = pack.addProvider(FFBlockTagsProvider::new);
		pack.addProvider((output, registriesFuture) -> new FFItemTagsProvider(output, registriesFuture, ffBlockTagsProvider));
		pack.addProvider(
				(output, registriesFuture) -> new FFPoiTypeTagsProvider(output, Registries.POINT_OF_INTEREST_TYPE, registriesFuture));
		pack.addProvider((output, registriesFuture) -> new FFDamageTypeTagsProvider(output, Registries.DAMAGE_TYPE, registriesFuture));
		pack.addProvider(FFEntityTypeTagsProvider::new);
		FabricTagsProvider.BlockTagsProvider seasonalBlockTagsProvider = pack.addProvider(SeasonalBlockTagsProvider::new);
		pack.addProvider((output, registriesFuture) -> new SeasonalItemTagsProvider(output, registriesFuture, seasonalBlockTagsProvider));
		pack.addProvider(FFBiomeTagsProvider::new);
		pack.addProvider(FFModelProvider::new);
		pack.addProvider(FFRecipeProvider::new);
		pack.addProvider(FFDynamicRegistryProvider::new);
		pack.addProvider(KiwiLanguageProvider::new);
		pack.addProvider(CherryBlockLoot::new);
		pack.addProvider(PomegranateBlockLoot::new);
		pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("food"));
		pack.addProvider(FoodBlockLoot::new);
		pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("gadget"));
		pack.addProvider(GadgetBlockLoot::new);
		pack = fabricDataGenerator.createBuiltinResourcePack(FruitfulFun.id("farmersdelight"));
		pack.addProvider(FarmersDelightBlockLoot::new);
	}
}
