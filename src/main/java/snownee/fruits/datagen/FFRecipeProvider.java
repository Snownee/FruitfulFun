package snownee.fruits.datagen;

import static snownee.fruits.CoreModule.CITRUS_BOAT;
import static snownee.fruits.CoreModule.CITRUS_BUTTON;
import static snownee.fruits.CoreModule.CITRUS_CHEST_BOAT;
import static snownee.fruits.CoreModule.CITRUS_DOOR;
import static snownee.fruits.CoreModule.CITRUS_FENCE;
import static snownee.fruits.CoreModule.CITRUS_FENCE_GATE;
import static snownee.fruits.CoreModule.CITRUS_HANGING_SIGN_ITEM;
import static snownee.fruits.CoreModule.CITRUS_LOG;
import static snownee.fruits.CoreModule.CITRUS_PLANKS;
import static snownee.fruits.CoreModule.CITRUS_PRESSURE_PLATE;
import static snownee.fruits.CoreModule.CITRUS_SHELF;
import static snownee.fruits.CoreModule.CITRUS_SIGN;
import static snownee.fruits.CoreModule.CITRUS_SLAB;
import static snownee.fruits.CoreModule.CITRUS_STAIRS;
import static snownee.fruits.CoreModule.CITRUS_TRAPDOOR;
import static snownee.fruits.CoreModule.CITRUS_WALL_SIGN;
import static snownee.fruits.CoreModule.CITRUS_WOOD;
import static snownee.fruits.CoreModule.GRAPEFRUIT;
import static snownee.fruits.CoreModule.SNOWFLAKE_BANNER_PATTERN;
import static snownee.fruits.CoreModule.STRIPPED_CITRUS_LOG;
import static snownee.fruits.CoreModule.STRIPPED_CITRUS_WOOD;
import static snownee.fruits.cherry.CherryModule.HEART_BANNER_PATTERN;
import static snownee.fruits.cherry.CherryModule.REDLOVE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_BOAT;
import static snownee.fruits.cherry.CherryModule.REDLOVE_BUTTON;
import static snownee.fruits.cherry.CherryModule.REDLOVE_CHEST_BOAT;
import static snownee.fruits.cherry.CherryModule.REDLOVE_DOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_FENCE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_FENCE_GATE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_HANGING_SIGN_ITEM;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.REDLOVE_PLANKS;
import static snownee.fruits.cherry.CherryModule.REDLOVE_PRESSURE_PLATE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SHELF;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SIGN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SLAB;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SLIDING_DOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_STAIRS;
import static snownee.fruits.cherry.CherryModule.REDLOVE_TRAPDOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_WALL_SIGN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_WOOD;
import static snownee.fruits.cherry.CherryModule.STRIPPED_REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.STRIPPED_REDLOVE_WOOD;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.compat.farmersdelight.FarmersDelightModule;
import snownee.fruits.food.FoodModule;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.recipe.AlternativesIngredientBuilder;
import snownee.kiwi.recipe.ModuleLoadedCondition;
import snownee.kiwi.recipe.RecipeUtil;

public class FFRecipeProvider extends FabricRecipeProvider {
	public static final BlockFamily CITRUS_FAMILY = BlockFamilies.familyBuilder(CITRUS_PLANKS.get())
			.button(CITRUS_BUTTON.get())
			.fence(CITRUS_FENCE.get())
			.fenceGate(CITRUS_FENCE_GATE.get())
			.pressurePlate(CITRUS_PRESSURE_PLATE.get())
			.sign(CITRUS_SIGN.get(), CITRUS_WALL_SIGN.get())
			.slab(CITRUS_SLAB.get())
			.stairs(CITRUS_STAIRS.get())
			.door(CITRUS_DOOR.get())
			.trapdoor(CITRUS_TRAPDOOR.get())
			.recipeGroupPrefix("wooden")
			.recipeUnlockedBy("has_planks")
			.getFamily();
	public static final BlockFamily REDLOVE_FAMILY = BlockFamilies.familyBuilder(REDLOVE_PLANKS.get())
			.button(REDLOVE_BUTTON.get())
			.fence(REDLOVE_FENCE.get())
			.fenceGate(REDLOVE_FENCE_GATE.get())
			.pressurePlate(REDLOVE_PRESSURE_PLATE.get())
			.sign(REDLOVE_SIGN.get(), REDLOVE_WALL_SIGN.get())
			.slab(REDLOVE_SLAB.get())
			.stairs(REDLOVE_STAIRS.get())
			.door(REDLOVE_DOOR.get())
			.trapdoor(REDLOVE_TRAPDOOR.get())
			.recipeGroupPrefix("wooden")
			.recipeUnlockedBy("has_planks")
			.getFamily();

	public FFRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				oneToOneConversionRecipe(Items.PINK_DYE, CherryModule.PEACH_PINK_PETALS.get().asItem(), "pink_dye");
				generateRecipes(CITRUS_FAMILY, FeatureFlagSet.of(FeatureFlags.VANILLA));
				generateRecipes(REDLOVE_FAMILY, FeatureFlagSet.of(FeatureFlags.VANILLA));
				oneToOneConversionRecipe(REDLOVE_SLIDING_DOOR.get(), REDLOVE_DOOR.get(), null);
				oneToOneConversionRecipe(REDLOVE_DOOR.get(), REDLOVE_SLIDING_DOOR.get(), null);
				planksFromLogs(CITRUS_PLANKS.get(), FFItemTagsProvider.CITRUS_LOGS, 4);
				planksFromLogs(REDLOVE_PLANKS.get(), FFItemTagsProvider.REDLOVE_LOGS, 4);
				woodFromLogs(CITRUS_WOOD.get(), CITRUS_LOG.get());
				woodFromLogs(REDLOVE_WOOD.get(), REDLOVE_LOG.get());
				woodFromLogs(STRIPPED_CITRUS_WOOD.get(), STRIPPED_CITRUS_LOG.get());
				woodFromLogs(STRIPPED_REDLOVE_WOOD.get(), STRIPPED_REDLOVE_LOG.get());
				woodenBoat(CITRUS_BOAT.get(), CITRUS_PLANKS.get());
				woodenBoat(REDLOVE_BOAT.get(), REDLOVE_PLANKS.get());
				chestBoat(CITRUS_CHEST_BOAT.get(), CITRUS_BOAT.get());
				chestBoat(REDLOVE_CHEST_BOAT.get(), REDLOVE_BOAT.get());
				hangingSign(CITRUS_HANGING_SIGN_ITEM.get(), STRIPPED_REDLOVE_LOG.get());
				hangingSign(REDLOVE_HANGING_SIGN_ITEM.get(), STRIPPED_CITRUS_LOG.get());
				shelf(CITRUS_SHELF.get(), STRIPPED_CITRUS_LOG.get());
				shelf(REDLOVE_SHELF.get(), STRIPPED_REDLOVE_LOG.get());
				flowerCrown(output, CherryModule.CHERRY_CROWN.get(), Items.CHERRY_LEAVES);
				flowerCrown(output, CherryModule.REDLOVE_CROWN.get(), CherryModule.REDLOVE_LEAVES.get());

				shapeless(RecipeCategory.MISC, HEART_BANNER_PATTERN.get())
						.requires(Items.PAPER)
						.requires(REDLOVE.get())
						.unlockedBy("has_redlove", has(REDLOVE.get()))
						.save(output);
				shapeless(RecipeCategory.MISC, SNOWFLAKE_BANNER_PATTERN.get())
						.requires(Items.PAPER)
						.requires(GRAPEFRUIT.get())
						.unlockedBy("has_grapefruit", has(GRAPEFRUIT.get()))
						.save(output);

				RecipeOutput beeExporter = withConditions(
						output,
						new ModuleLoadedCondition(FruitfulFun.id("bee")));
				oneToOneConversionRecipe(Items.GLASS_BOTTLE, BeeModule.MUTAGEN.get(), null);
				shaped(RecipeCategory.TOOLS, BeeModule.INSPECTOR.get())
						.pattern("A")
						.pattern("B")
						.pattern("C")
						.define('A', Items.GLASS_PANE)
						.define('B', Items.COPPER_INGOT)
						.define('C', Items.STICK)
						.unlockedBy("has_item", has(Items.BEEHIVE))
						.save(beeExporter);

				if (Hooks.farmersdelight) {
					RecipeOutput fdExporter = withConditions(
							output,
							new ModuleLoadedCondition(FruitfulFun.id("farmersdelight")));
					cabinet(fdExporter, FarmersDelightModule.CITRUS_CABINET.get(), CITRUS_SLAB.get(), CITRUS_TRAPDOOR.get());
					cabinet(fdExporter, FarmersDelightModule.REDLOVE_CABINET.get(), REDLOVE_SLAB.get(), REDLOVE_TRAPDOOR.get());
				}

				RecipeOutput foodExporter = withConditions(
						output,
						new ModuleLoadedCondition(FruitfulFun.id("food")));
				RecipeOutput foodExporterNoFD = withConditions(
						output,
						new ModuleLoadedCondition(FruitfulFun.id("food")),
						ResourceConditions.not(new ModuleLoadedCondition(FruitfulFun.id("farmersdelight"))));
				shapeless(RecipeCategory.FOOD, FoodModule.DONAUWELLE.get())
						.requires(CherryModule.REDLOVE.get())
						.requires(Items.COCOA_BEANS)
//						.requires(AlternativesIngredientBuilder.of(registries)
//								.add(DefaultCustomIngredients.any(
//										tag(AbstractModule.itemTag("c", "chocolates")),
//										tag(AbstractModule.itemTag("c", "chocolatebar")))
//								)
//								.add(Items.COCOA_BEANS)
//								.toVanilla())
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:cream")
								.add("#c:milk")
								.add(ConventionalItemTags.MILK_BUCKETS)
								.toVanilla())
						.requires(ConventionalItemTags.EGGS)
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:flour")
								.add("#c:grain/wheat")
								.add(Items.WHEAT)
								.toVanilla())
						.requires(Items.SUGAR)
						.unlockedBy("has_item", has(CherryModule.REDLOVE.get()))
						.save(foodExporter);

				ResourceCondition hasRice = ResourceConditions.or(
						ResourceConditions.tagsPopulated(AbstractModule.itemTag("c", "grain/rice")),
						ResourceConditions.tagsPopulated(AbstractModule.itemTag("c", "seeds/rice"))
				);
				RecipeOutput riceWithFruitsExporter = withConditions(
						output,
						hasRice,
						new ModuleLoadedCondition(FruitfulFun.id("food")),
						ResourceConditions.not(new ModuleLoadedCondition(FruitfulFun.id("farmersdelight"))));
				shapeless(RecipeCategory.FOOD, FoodModule.RICE_WITH_FRUITS.get())
						.requires(AbstractModule.itemTag("c", "fruits/tangerine"))
						.requires(AbstractModule.itemTag("c", "fruits/apple"))
						.requires(ConventionalItemTags.FRUIT_FOODS)
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:grain/rice")
								.add("#c:seeds/rice")
								.toVanilla())
						.requires(Items.BAMBOO)
						.unlockedBy("has_item", has(AbstractModule.itemTag("c", "fruits/tangerine")))
						.save(riceWithFruitsExporter);

				shapeless(RecipeCategory.FOOD, FoodModule.GRAPEFRUIT_PANNA_COTTA.get())
						.requires(AbstractModule.itemTag("c", "fruits/grapefruit"))
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:cream")
								.add("#c:milk")
								.add(ConventionalItemTags.MILK_BUCKETS)
								.toVanilla())
						.requires(ConventionalItemTags.EGGS)
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:gelatin")
								.add("#c:gelatine")
								.add("#c:slime_balls")
								.add(Items.SLIME_BALL)
								.toVanilla())
						.requires(Items.SUGAR)
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:vannila")
								.add("#c:crops/vanilla")
								.allowEmpty()
								.toVanilla())
						.unlockedBy("has_item", has(CoreModule.GRAPEFRUIT.get()))
						.save(foodExporterNoFD);

				shapeless(RecipeCategory.FOOD, FoodModule.HONEY_POMELO_TEA.get())
						.requires(AbstractModule.itemTag("c", "fruits/pomelo"))
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:crops/mint")
								.add("#c:leaves/mint")
								.allowEmpty()
								.toVanilla())
						.requires(Items.HONEY_BOTTLE)
						.requires(Items.SUGAR)
						.unlockedBy("has_item", has(AbstractModule.itemTag("c", "fruits/pomelo")))
						.save(RecipeUtil.withNoRemainders(foodExporterNoFD));

				shapeless(RecipeCategory.FOOD, FoodModule.CHORUS_FRUIT_PIE.get())
						.requires(Items.CHORUS_FRUIT)
						.requires(Items.CHORUS_FRUIT)
						.requires(ConventionalItemTags.EGGS)
						.requires(Items.SUGAR)
						.unlockedBy("has_item", has(Items.CHORUS_FRUIT))
						.save(foodExporter);

				shaped(RecipeCategory.FOOD, FoodModule.CHORUS_FRUIT_PIE.get())
						.define('#', FoodModule.CHORUS_FRUIT_PIE_SLICE.get())
						.pattern("##")
						.pattern("##")
						.unlockedBy("has_item", has(FoodModule.CHORUS_FRUIT_PIE_SLICE.get()))
						.save(foodExporter, "chorus_fruit_pie_packing");

				shapeless(RecipeCategory.FOOD, FoodModule.LEMON_ROAST_CHICKEN_BLOCK.get())
						.requires(AbstractModule.itemTag("c", "fruits/lemon"))
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:flowers/lavender")
								.add("#c:fruits/lemon")
								.toVanilla())
						.requires(AlternativesIngredientBuilder.of(registries)
								.add("#c:vegetables/onion")
								.add(Items.POTATO)
								.toVanilla())
						.requires(Items.COOKED_CHICKEN)
						.requires(Items.BOWL)
						.unlockedBy("has_item", has(AbstractModule.itemTag("c", "fruits/lemon")))
						.save(foodExporter, "lemon_roast_chicken");

				RecipeOutput noBeeExporter = withConditions(
						output,
						ResourceConditions.not(new ModuleLoadedCondition(FruitfulFun.id("bee"))));
				sapling(
						noBeeExporter, CoreModule.GRAPEFRUIT_SAPLING,
						CoreModule.LEMON_SAPLING.get(),
						CoreModule.POMELO_SAPLING.get(),
						CoreModule.ORANGE_SAPLING.get());
				sapling(
						noBeeExporter, CoreModule.LEMON_SAPLING,
						CoreModule.LIME_SAPLING.get(),
						CoreModule.CITRON_SAPLING.get());
				sapling(
						noBeeExporter, CoreModule.POMELO_SAPLING,
						CoreModule.TANGERINE_SAPLING.get(),
						CoreModule.CITRON_SAPLING.get());
				sapling(
						noBeeExporter, CoreModule.ORANGE_SAPLING,
						CoreModule.TANGERINE_SAPLING.get(),
						CoreModule.LIME_SAPLING.get());
				sapling(
						noBeeExporter, CherryModule.REDLOVE_SAPLING,
						CoreModule.APPLE_SAPLING.get(),
						CherryModule.CHERRY_SAPLING.get(),
						Items.WITHER_ROSE);
				sapling(
						noBeeExporter, CherryModule.CHERRY_SAPLING,
						Items.CHERRY_SAPLING,
						Items.PINK_PETALS);
				sapling(
						noBeeExporter, PomegranateModule.POMEGRANATE_SAPLING,
						CoreModule.APPLE_SAPLING.get(),
						Items.SPORE_BLOSSOM);

				RecipeOutput gadgetExporter = withConditions(
						output,
						new ModuleLoadedCondition(FruitfulFun.id("gadget")));
				shaped(RecipeCategory.DECORATIONS, GadgetModule.BUZZY_CRAFTER.get())
						.pattern("THT")
						.pattern("CCC")
						.pattern("THT")
						.define('T', Items.CRAFTING_TABLE)
						.define('H', Items.HOPPER)
						.define('C', Items.HONEYCOMB)
						.unlockedBy("has_item", has(Items.HONEYCOMB))
						.save(gadgetExporter);
				scentedCandle(gadgetExporter, tag(FFItemTagsProvider.TULIPS), GadgetModule.WEAK_CANDLE);
				scentedCandle(gadgetExporter, Ingredient.of(CherryModule.CHERRY.get()), GadgetModule.WANDERING_TRADER_CANDLE);
				scentedCandle(gadgetExporter, Ingredient.of(Items.SUNFLOWER), GadgetModule.PHANTOM_CANDLE);
				scentedCandle(gadgetExporter, Ingredient.of(Items.PITCHER_PLANT), GadgetModule.ENDER_CANDLE);

				SmithingTransformRecipeBuilder.smithing(
								tag(FFItemTagsProvider.GADGET_TOKEN),
								Ingredient.of(Items.SHIELD),
								Ingredient.of(Items.HONEYCOMB_BLOCK),
								RecipeCategory.TOOLS,
								GadgetModule.BUZZY_SHIELD.get())
						.unlocks("has_crafter", has(GadgetModule.BUZZY_CRAFTER.get()))
						.save(gadgetExporter, getSimpleRecipeName(GadgetModule.BUZZY_SHIELD.get()));
			}

			public void scentedCandle(RecipeOutput output, Ingredient addition, KiwiGO<? extends ItemLike> result) {
				SmithingTransformRecipeBuilder.smithing(
								tag(FFItemTagsProvider.GADGET_TOKEN),
								tag(ItemTags.CANDLES),
								addition,
								RecipeCategory.DECORATIONS,
								result.get().asItem())
						.unlocks("has_crafter", has(GadgetModule.BUZZY_CRAFTER.get()))
						.save(output, getSimpleRecipeName(result.get()));
			}

			public void sapling(RecipeOutput output, KiwiGO<? extends ItemLike> result, ItemLike... inputs) {
				ShapelessRecipeBuilder builder = shapeless(RecipeCategory.MISC, result.get());
				for (ItemLike input : inputs) {
					builder.requires(input);
					String id = BuiltInRegistries.ITEM.getKey(input.asItem()).getPath();
					builder.unlockedBy("has_" + id, has(input));
				}
				String id = BuiltInRegistries.ITEM.getKey(result.get().asItem()).getPath();
				builder.save(output, "no_hybrid/" + id);
			}

			public void flowerCrown(RecipeOutput output, ItemLike wreath, ItemLike leaves) {
				shaped(RecipeCategory.MISC, wreath)
						.pattern(" # ")
						.pattern("# #")
						.pattern(" # ")
						.define('#', leaves)
						.unlockedBy("has_item", has(leaves))
						.save(output);
			}

			public void cabinet(RecipeOutput output, ItemLike cabinet, ItemLike slab, ItemLike trapdoor) {
				shaped(RecipeCategory.DECORATIONS, cabinet)
						.pattern("SSS")
						.pattern("T T")
						.pattern("SSS")
						.define('S', slab)
						.define('T', trapdoor)
						.unlockedBy("has_item", has(trapdoor))
						.save(output);
			}
		};
	}

	@Override
	public String getName() {
		return "Fruitful Fun - Recipes";
	}
}
