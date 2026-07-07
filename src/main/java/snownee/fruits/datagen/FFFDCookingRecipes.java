package snownee.fruits.datagen;

import static snownee.kiwi.AbstractModule.itemTag;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.Nullable;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.food.FoodModule;
import snownee.kiwi.recipe.AlternativesIngredientBuilder;
import vectorwing.farmersdelight.common.crafting.CookingPotBookCategory;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;
import vectorwing.farmersdelight.data.recipe.CookingRecipes;

public class FFFDCookingRecipes extends FabricRecipeProvider {
	public FFFDCookingRecipes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				cooking(FoodModule.GRAPEFRUIT_PANNA_COTTA, null)
						.addIngredient(itemTag("c:crops/grapefruit"))
						.addIngredient(AlternativesIngredientBuilder.of(registries)
								.add("#c:cream")
								.add("#c:drinks/milk")
								.add(ConventionalItemTags.MILK_BUCKETS)
								.toVanilla())
						.addIngredient(ConventionalItemTags.EGGS)
						.addIngredient(AlternativesIngredientBuilder.of(registries)
								.add("#c:gelatin")
								.add("#c:gelatine")
								.add("#c:slime_balls")
								.add(Items.SLIME_BALL)
								.toVanilla())
						.addIngredient(Items.SUGAR)
						.addIngredient(AlternativesIngredientBuilder.of(registries)
								.add("#c:vanilla")
								.add("#c:crops/vanilla")
								.allowEmpty()
								.toVanilla())
						.setRecipeBookCategory(CookingPotBookCategory.MISC)
						.unlockedBy("has_item", has(itemTag("c:crops/grapefruit")))
						.save(output);

				cooking(FoodModule.HONEY_POMELO_TEA, Items.GLASS_BOTTLE)
						.addIngredient(itemTag("c:crops/pomelo"))
						.addIngredient(AlternativesIngredientBuilder.of(registries)
								.add("#c:crops/mint")
								.add("#c:leaves/mint")
								.allowEmpty()
								.toVanilla())
						.addIngredient(Items.HONEY_BOTTLE)
						.addIngredient(Items.SUGAR)
						.setRecipeBookCategory(CookingPotBookCategory.DRINKS)
						.unlockedBy("has_item", has(itemTag("c:crops/pomelo")))
						.save(output);

				cooking(FoodModule.RICE_WITH_FRUITS, Items.BAMBOO)
						.addIngredient(itemTag("c:crops/tangerine"))
						.addIngredient(itemTag("c:crops/apple"))
						.addIngredient(ConventionalItemTags.FRUIT_FOODS)
						.addIngredient(AlternativesIngredientBuilder.of(registries)
								.add("#c:crops/rice")
								.add("#c:seeds/rice")
								.toVanilla())
						.setRecipeBookCategory(CookingPotBookCategory.MEALS)
						.unlockedBy("has_item", has(itemTag("c:crops/tangerine")))
						.save(output);
			}

			CookingPotRecipeBuilder cooking(ItemLike result, @Nullable ItemLike container) {
				HolderLookup.RegistryLookup<Item> items = Objects.requireNonNull(registries).lookupOrThrow(Registries.ITEM);
				return CookingPotRecipeBuilder.cookingPotRecipe(
						items,
						result,
						1,
						CookingRecipes.NORMAL_COOKING,
						CookingRecipes.MEDIUM_EXP,
						container);
			}
		};
	}

	@Override
	public String getName() {
		return "Fruitful Fun - Farmers Delight Cooking Recipes";
	}
}
