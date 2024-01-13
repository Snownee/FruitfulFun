//package snownee.fruits.datagen;
//
//import java.util.function.Consumer;
//
//import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
//import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
//import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
//import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.data.recipes.FinishedRecipe;
//import net.minecraft.data.recipes.RecipeCategory;
//import net.minecraft.data.recipes.ShapedRecipeBuilder;
//import net.minecraft.data.recipes.ShapelessRecipeBuilder;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.level.ItemLike;
//import snownee.fruits.CoreModule;
//import snownee.fruits.FruitfulFun;
//import snownee.fruits.Hooks;
//import snownee.fruits.bee.BeeModule;
//import snownee.fruits.cherry.CherryModule;
//import snownee.fruits.compat.farmersdelight.FarmersDelightModule;
//import snownee.fruits.food.FoodModule;
//import snownee.kiwi.AbstractModule;
//import snownee.kiwi.KiwiGO;
//import snownee.kiwi.recipe.AlternativesIngredientBuilder;
//import snownee.kiwi.recipe.ModuleLoadedCondition;
//import snownee.kiwi.recipe.crafting.KiwiShapelessRecipeBuilder;
//
//public class FFRecipeProvider extends FabricRecipeProvider {
//	public FFRecipeProvider(FabricDataOutput output) {
//		super(output);
//	}
//
//	@Override
//	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
//		oneToOneConversionRecipe(exporter, Items.PINK_DYE, CherryModule.PEACH_PINK_PETALS.get().asItem(), "pink_dye");
//		woodenBoat(exporter, Items.OAK_BOAT, CoreModule.CITRUS_PLANKS.get());
//		woodenBoat(exporter, Items.CHERRY_BOAT, CherryModule.REDLOVE_PLANKS.get());
//		flowerCrown(exporter, CherryModule.CHERRY_CROWN.get(), CherryModule.CHERRY_LEAVES.get());
//		flowerCrown(exporter, CherryModule.REDLOVE_CROWN.get(), CherryModule.REDLOVE_LEAVES.get());
//
//		Consumer<FinishedRecipe> beeExporter = withConditions(exporter, ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "bee")));
//		oneToOneConversionRecipe(beeExporter, Items.GLASS_BOTTLE, BeeModule.MUTAGEN.get(), null);
//		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BeeModule.INSPECTOR.get())
//				.pattern("A")
//				.pattern("B")
//				.pattern("C")
//				.define('A', Items.GLASS_PANE)
//				.define('B', Items.COPPER_INGOT)
//				.define('C', Items.STICK)
//				.unlockedBy("has_item", has(Items.BEEHIVE))
//				.save(beeExporter);
//
//		if (Hooks.farmersdelight) {
//			Consumer<FinishedRecipe> fdExporter = withConditions(exporter, ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "farmersdelight")));
//			cabinet(fdExporter, FarmersDelightModule.CITRUS_CABINET.get(), CoreModule.CITRUS_SLAB.get(), CoreModule.CITRUS_TRAPDOOR.get());
//			cabinet(fdExporter, FarmersDelightModule.REDLOVE_CABINET.get(), CherryModule.REDLOVE_SLAB.get(), CherryModule.REDLOVE_TRAPDOOR.get());
//		}
//
//		Consumer<FinishedRecipe> foodExporter = withConditions(exporter, ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "food")));
//		Consumer<FinishedRecipe> foodExporterNoFD = withConditions(exporter,
//				ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "food")),
//				DefaultResourceConditions.not(ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "farmersdelight"))));
//		KiwiShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodModule.DONAUWELLE.get())
//				.requires(CherryModule.REDLOVE.get())
//				.requires(AlternativesIngredientBuilder.of()
//						.add(DefaultCustomIngredients.any(
//								Ingredient.of(AbstractModule.itemTag("c", "chocolates")),
//								Ingredient.of(AbstractModule.itemTag("c", "chocolatebar")))
//						)
//						.add(Items.COCOA_BEANS)
//						.build().toVanilla())
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:cream")
//						.add("#c:milk")
//						.add(Items.MILK_BUCKET)
//						.build().toVanilla())
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:eggs")
//						.add(Items.EGG)
//						.build().toVanilla())
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:flour")
//						.add("#c:grain/wheat")
//						.add(Items.WHEAT)
//						.build().toVanilla())
//				.requires(Items.SUGAR)
//				.unlockedBy("has_item", has(CherryModule.REDLOVE.get()))
//				.save(foodExporter);
//
//		ConditionJsonProvider hasRice = DefaultResourceConditions.or(
//				DefaultResourceConditions.tagsPopulated(AbstractModule.itemTag("c", "grain/rice")),
//				DefaultResourceConditions.tagsPopulated(AbstractModule.itemTag("c", "seeds/rice"))
//		);
//		Consumer<FinishedRecipe> riceWithFruitsExporter = withConditions(exporter,
//				hasRice,
//				ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "food")),
//				DefaultResourceConditions.not(ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "farmersdelight"))));
//		KiwiShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodModule.RICE_WITH_FRUITS.get())
//				.requires(AbstractModule.itemTag("c", "fruits/tangerine"))
//				.requires(AbstractModule.itemTag("c", "fruits/apple"))
//				.requires(AbstractModule.itemTag("c", "fruits"))
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:grain/rice")
//						.add("#c:seeds/rice")
//						.build().toVanilla())
//				.requires(Items.BAMBOO)
//				.unlockedBy("has_item", has(AbstractModule.itemTag("c", "fruits/tangerine")))
//				.save(riceWithFruitsExporter);
//
//		KiwiShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodModule.GRAPEFRUIT_PANNA_COTTA.get())
//				.requires(AbstractModule.itemTag("c", "fruits/grapefruit"))
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:cream")
//						.add("#c:milk")
//						.add(Items.MILK_BUCKET)
//						.build().toVanilla())
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:eggs")
//						.add(Items.EGG)
//						.build().toVanilla())
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:gelatin")
//						.add("#c:gelatine")
//						.add("#c:slime_balls")
//						.add(Items.SLIME_BALL)
//						.build().toVanilla())
//				.requires(Items.SUGAR)
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:vannila")
//						.add("#c:crops/vanilla")
//						.add(Ingredient.EMPTY)
//						.build().toVanilla())
//				.unlockedBy("has_item", has(CoreModule.GRAPEFRUIT.get()))
//				.save(foodExporterNoFD);
//
//		KiwiShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodModule.HONEY_POMELO_TEA.get())
//				.requires(AbstractModule.itemTag("c", "fruits/pomelo"))
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:crops/mint")
//						.add("#c:leaves/mint")
//						.add(Ingredient.EMPTY)
//						.build().toVanilla())
//				.requires(Items.HONEY_BOTTLE)
//				.requires(Items.SUGAR)
//				.noContainers()
//				.unlockedBy("has_item", has(AbstractModule.itemTag("c", "fruits/pomelo")))
//				.save(foodExporterNoFD);
//
//		KiwiShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodModule.LEMON_ROAST_CHICKEN_BLOCK.get())
//				.requires(AbstractModule.itemTag("c", "fruits/lemon"))
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:flowers/lavender")
//						.add("#c:fruits/lemon")
//						.build().toVanilla())
//				.requires(AlternativesIngredientBuilder.of()
//						.add("#c:vegetables/onion")
//						.add(Items.POTATO)
//						.build().toVanilla())
//				.requires(Items.COOKED_CHICKEN)
//				.requires(Items.BOWL)
//				.unlockedBy("has_item", has(AbstractModule.itemTag("c", "fruits/lemon")))
//				.save(foodExporter, "lemon_roast_chicken");
//
//		Consumer<FinishedRecipe> noBeeExporter = withConditions(exporter, DefaultResourceConditions.not(ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "bee"))));
//		sapling(noBeeExporter, CoreModule.GRAPEFRUIT_SAPLING,
//				CoreModule.LEMON_SAPLING.get(),
//				CoreModule.POMELO_SAPLING.get(),
//				CoreModule.ORANGE_SAPLING.get());
//		sapling(noBeeExporter, CoreModule.LEMON_SAPLING,
//				CoreModule.LIME_SAPLING.get(),
//				CoreModule.CITRON_SAPLING.get());
//		sapling(noBeeExporter, CoreModule.POMELO_SAPLING,
//				CoreModule.TANGERINE_SAPLING.get(),
//				CoreModule.CITRON_SAPLING.get());
//		sapling(noBeeExporter, CoreModule.ORANGE_SAPLING,
//				CoreModule.TANGERINE_SAPLING.get(),
//				CoreModule.LIME_SAPLING.get());
//		sapling(noBeeExporter, CherryModule.REDLOVE_SAPLING,
//				CoreModule.APPLE_SAPLING.get(),
//				CherryModule.CHERRY_SAPLING.get(),
//				Items.WITHER_ROSE);
//		sapling(noBeeExporter, CherryModule.CHERRY_SAPLING,
//				Items.CHERRY_SAPLING,
//				Items.PINK_PETALS);
//	}
//
//	public static void sapling(Consumer<FinishedRecipe> exporter, KiwiGO<? extends ItemLike> result, ItemLike... inputs) {
//		ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get());
//		for (ItemLike input : inputs) {
//			builder.requires(input);
//			String id = BuiltInRegistries.ITEM.getKey(input.asItem()).getPath();
//			builder.unlockedBy("has_" + id, has(input));
//		}
//		String id = BuiltInRegistries.ITEM.getKey(result.get().asItem()).getPath();
//		builder.save(exporter, "no_hybrid/" + id);
//	}
//
//	public static void flowerCrown(Consumer<FinishedRecipe> exporter, ItemLike wreath, ItemLike leaves) {
//		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, wreath)
//				.pattern(" # ")
//				.pattern("# #")
//				.pattern(" # ")
//				.define('#', leaves)
//				.unlockedBy("has_item", has(leaves))
//				.save(exporter);
//	}
//
//	public static void cabinet(Consumer<FinishedRecipe> exporter, ItemLike cabinet, ItemLike slab, ItemLike trapdoor) {
//		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, cabinet)
//				.pattern("SSS")
//				.pattern("T T")
//				.pattern("SSS")
//				.define('S', slab)
//				.define('T', trapdoor)
//				.unlockedBy("has_item", has(trapdoor))
//				.save(exporter);
//	}
//}
