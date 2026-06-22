package snownee.fruits.compat.jei;

import java.util.List;
import java.util.Map;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.fruits.compat.lychee.LycheeCompat;
import snownee.fruits.ritual.RitualModule;

@JeiPlugin
public class FFJEICompat implements IModPlugin {

	public static final Identifier UID = FruitfulFun.id("main");

	public FFJEICompat() {
		JEICompat.addCategoryFactoryProvider($ -> {
			if (Hooks.bee) {
				$.put(BeeModule.RECIPE_TYPE.get().categoryId, $$ -> new HybridizingCategory(BeeModule.RECIPE_TYPE.get()));
			}
			if (Hooks.ritual) {
				$.put(RitualModule.RECIPE_TYPE.get().categoryId, $$ -> new DragonRitualCategory(RitualModule.RECIPE_TYPE.get()));
			}
		});
	}

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		if (Hooks.ritual) {
			ItemStack dragonHead = Items.DRAGON_HEAD.getDefaultInstance();
			ItemStack pie = LycheeCompat.pieItem.get();
			for (BaseJEICategory<?, ?> category : JEICompat.CATEGORIES.getOrDefault(RitualModule.RECIPE_TYPE.get().categoryId, Map.of())
					.values()) {
				registration.addRecipeCatalyst(dragonHead, category.getRecipeType());
				registration.addRecipeCatalyst(pie, category.getRecipeType());
			}
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (FFCommonConfig.isMutagenRecipeEnabled()) {
			NoHashBrewingRecipe brewingRecipe = new NoHashBrewingRecipe(
					BeeModule.MUTAGEN.key(),
					List.of(new ItemStack(MutagenItem.BREWING_ITEM)),
					List.of(Items.POTION.getDefaultInstance()),
					new ItemStack(BeeModule.MUTAGEN.get()), 1);
			registration.addRecipes(RecipeTypes.BREWING, List.of(brewingRecipe));
		}

		LycheeCompat.addInformation(registration::addItemStackInfo);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		if (FFCommonConfig.isMutagenRecipeEnabled()) {
			IRecipeLookup<IJeiBrewingRecipe> recipeLookup = jeiRuntime.getRecipeManager().createRecipeLookup(RecipeTypes.BREWING);
			List<IJeiBrewingRecipe> recipes = recipeLookup.get().filter($ -> {
				ItemStack output = $.getPotionOutput();
				return BeeModule.MUTAGEN.is(output) && MutagenItem.getCodename(output).isPresent();
			}).toList();
			jeiRuntime.getRecipeManager().hideRecipes(RecipeTypes.BREWING, recipes);
		}
	}
}
