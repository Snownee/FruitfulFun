package snownee.fruits.compat.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.bee.genetics.MutagenItem;

@JeiPlugin
public class JEICompat implements IModPlugin {

	public static final ResourceLocation UID = new ResourceLocation(FruitfulFun.ID, "hybridizing");
	public static final RecipeType<HybridizingRecipe> RECIPE_TYPE = new RecipeType<>(UID, HybridizingRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		if (Hooks.bee) {
			registration.addRecipeCategories(new HybridizingCategory(registration.getJeiHelpers().getGuiHelper()));
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Hooks.bee) {
			ClientLevel world = Minecraft.getInstance().level;
			RecipeManager recipeManager = world.getRecipeManager();
			registration.addRecipes(RECIPE_TYPE, List.copyOf(recipeManager.getAllRecipesFor(BeeModule.RECIPE_TYPE)));

			IJeiBrewingRecipe brewingRecipe = registration.getVanillaRecipeFactory().createBrewingRecipe(
					List.of(new ItemStack(MutagenItem.BREWING_ITEM)),
					Items.POTION.getDefaultInstance(),
					new ItemStack(BeeModule.MUTAGEN.get()));
			registration.addRecipes(RecipeTypes.BREWING, List.of(brewingRecipe));
		}

		if (FFCommonConfig.appleSaplingFromHeroOfTheVillage || FFCommonConfig.villageAppleTreeWorldGen) {
			String info = "";
			if (FFCommonConfig.appleSaplingFromHeroOfTheVillage) {
				info = I18n.get("gui.fruitfulfun.tip.appleSaplingFromHeroOfTheVillage");
			}
			if (FFCommonConfig.villageAppleTreeWorldGen) {
				if (FFCommonConfig.appleSaplingFromHeroOfTheVillage) {
					info += "\n";
				}
				info += I18n.get("gui.fruitfulfun.tip.villageAppleTreeWorldGen");
			}
			ItemStack appleSapling = CoreModule.APPLE_SAPLING.itemStack();
			registration.addIngredientInfo(appleSapling, VanillaTypes.ITEM_STACK, Component.literal(info));
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		if (Hooks.bee) {
			IRecipeLookup<IJeiBrewingRecipe> recipeLookup = jeiRuntime.getRecipeManager().createRecipeLookup(RecipeTypes.BREWING);
			List<IJeiBrewingRecipe> recipes = recipeLookup.get().filter($ -> {
				ItemStack output = $.getPotionOutput();
				return BeeModule.MUTAGEN.is(output) && MutagenItem.getCodename(output).isPresent();
			}).toList();
			jeiRuntime.getRecipeManager().hideRecipes(RecipeTypes.BREWING, recipes);
		}
	}
}
