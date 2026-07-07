package snownee.fruits.datagen;

import static snownee.fruits.CoreModule.CITRUS_SLAB;
import static snownee.fruits.CoreModule.CITRUS_TRAPDOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SLAB;
import static snownee.fruits.cherry.CherryModule.REDLOVE_TRAPDOOR;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.FruitfulFun;
import snownee.fruits.compat.farmersdelight.FarmersDelightModule;
import snownee.fruits.food.FoodModule;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;
import vectorwing.farmersdelight.refabricated.ItemAbility;

public class FFFDCuttingRecipes extends FabricRecipeProvider {
	private HolderLookup.@Nullable Provider registries = null;

	public FFFDCuttingRecipes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		this.registries = registries;
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				cabinet(output, FarmersDelightModule.CITRUS_CABINET.get(), CITRUS_SLAB.get(), CITRUS_TRAPDOOR.get());
				cabinet(output, FarmersDelightModule.REDLOVE_CABINET.get(), REDLOVE_SLAB.get(), REDLOVE_TRAPDOOR.get());

				wood("citrus", FFRecipes.CITRUS_FAMILY, output);
				wood("redlove", FFRecipes.REDLOVE_FAMILY, output);
				HolderLookup.RegistryLookup<Item> items = Objects.requireNonNull(registries).lookupOrThrow(Registries.ITEM);
				CuttingBoardRecipeBuilder.cuttingRecipe(
						Ingredient.of(FoodModule.CHORUS_FRUIT_PIE.get()),
						Ingredient.of(items.getOrThrow(CommonTags.Items.TOOLS_KNIFE)),
						FoodModule.CHORUS_FRUIT_PIE_SLICE.get(),
						4).save(output);
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

	private void wood(String name, BlockFamily family, RecipeOutput output) {
		HolderLookup.RegistryLookup<Item> items = Objects.requireNonNull(registries).lookupOrThrow(Registries.ITEM);
		List<ItemLike> furniture = Lists.newArrayList();
		furniture.add(family.get(BlockFamily.Variant.DOOR));
		furniture.add(family.get(BlockFamily.Variant.TRAPDOOR));
		furniture.add(family.get(BlockFamily.Variant.SIGN));
		furniture.add(item(name + "_hanging_sign"));
		furniture.add(family.get(BlockFamily.Variant.FENCE));
		furniture.add(family.get(BlockFamily.Variant.FENCE_GATE));
		furniture.add(family.get(BlockFamily.Variant.PRESSURE_PLATE));
		furniture.add(family.get(BlockFamily.Variant.BUTTON));
		furniture.add(item(name + "_boat"));
		furniture.add(item(name + "_cabinet"));
		if ("redlove".equals(name)) {
			furniture.add(item(name + "_sliding_door"));
		}
		salvagePlankFromFurniture(items, output, name, family.getBaseBlock(), furniture.toArray(new ItemLike[0]));
		Ingredient hoes = matchesTool(items, ItemAbility.HOE_DIG, ItemTags.HOES);
		CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(item(name + "_chest_boat")), hoes, item(name + "_boat"))
				.addResult(Items.CHEST)
				.salvaging()
				.save(output);
		stripLogForBark(output, item(name + "_log"), item("stripped_" + name + "_log"));
	}

	private Item item(String id) {
		return Objects.requireNonNull(registries).lookupOrThrow(Registries.ITEM).getOrThrow(ResourceKey.create(
				Registries.ITEM,
				FruitfulFun.id(id))).value();
	}

	private void salvagePlankFromFurniture(
			HolderGetter<Item> holderGetter,
			RecipeOutput output,
			String name,
			ItemLike plank,
			ItemLike... furniture) {
		CuttingBoardRecipeBuilder.cuttingRecipe(
				Ingredient.of(furniture),
				matchesTool(holderGetter, ItemAbility.AXE_DIG, ItemTags.AXES),
				plank,
				1,
				0.75F).save(
				output,
				salvagingRecipe(name + "_furniture"));
	}

	private void stripLogForBark(RecipeOutput output, ItemLike log, ItemLike strippedLog) {
		CuttingBoardRecipeBuilder.cuttingRecipe(
						Ingredient.of(log),
						new ItemAbilityIngredient(ItemAbility.AXE_STRIP).toVanilla(),
						strippedLog)
				.addResult(ModItems.TREE_BARK.get())
				.addSound(SoundEvents.AXE_STRIP)
				.save(output);
	}

	private Ingredient matchesTool(HolderGetter<Item> holderGetter, ItemAbility toolAction, TagKey<Item> fallbackTag) {
		return DefaultCustomIngredients.any(
				new ItemAbilityIngredient(toolAction).toVanilla(),
				Ingredient.of(holderGetter.getOrThrow(fallbackTag)));
	}

	private static Identifier salvagingRecipe(String name) {
		return Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "salvaging/" + name);
	}

	@Override
	public String getName() {
		return "Fruitful Fun - Farmers Delight Cutting Recipes";
	}
}
