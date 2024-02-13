package snownee.fruits.compat.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.lychee.compat.jei.JEICompat;

@JeiPlugin
public class FFJEICompat implements IModPlugin {

	public static final ResourceLocation UID = new ResourceLocation(FruitfulFun.ID, "main");

	public FFJEICompat() {
		JEICompat.addCategoryFactoryProvider($ -> {
			if (Hooks.bee) {
				$.put(BeeModule.RECIPE_TYPE.get().categoryId, $$ -> new HybridizingCategory(BeeModule.RECIPE_TYPE.get()));
			}
		});
	}

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Hooks.bee) {
			NoHashBrewingRecipe brewingRecipe = new NoHashBrewingRecipe(
					List.of(new ItemStack(MutagenItem.BREWING_ITEM)),
					List.of(Items.POTION.getDefaultInstance()),
					new ItemStack(BeeModule.MUTAGEN.get()), 1);
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
