//package snownee.fruits.datagen;
//
//import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
//import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
//import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceLocation;
//import snownee.fruits.FruitfulFun;
//import snownee.fruits.cherry.datagen.CherryBlockLoot;
//import snownee.fruits.compat.farmersdelight.FarmersDelightBlockLoot;
//import snownee.fruits.food.datagen.FoodBlockLoot;
//
//public class FFDataGen implements DataGeneratorEntrypoint {
//	@Override
//	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
//		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
//		pack.addProvider((FabricDataOutput $) -> new CoreBlockLoot($));
//		pack.addProvider(FFAdvancements::new);
//		FFBlockTagsProvider blockTagsProvider = pack.addProvider(FFBlockTagsProvider::new);
//		pack.addProvider((output, registriesFuture) -> new FFItemTagsProvider(output, registriesFuture, blockTagsProvider));
//		pack.addProvider((output, registriesFuture) -> new FFPoiTypeTagsProvider(output, Registries.POINT_OF_INTEREST_TYPE, registriesFuture));
//		pack.addProvider(FFModelProvider::new);
//		pack.addProvider(FFRecipeProvider::new);
//		pack.addProvider(FFDynamicRegistryProvider::new);
//		pack.addProvider(FFLanguageProvider::new);
//		pack.addProvider(CherryBlockLoot::new);
//		pack = fabricDataGenerator.createBuiltinResourcePack(new ResourceLocation(FruitfulFun.ID, "food"));
//		pack.addProvider(FoodBlockLoot::new);
//		pack = fabricDataGenerator.createBuiltinResourcePack(new ResourceLocation(FruitfulFun.ID, "farmersdelight"));
//		pack.addProvider(FarmersDelightBlockLoot::new);
//	}
//}
