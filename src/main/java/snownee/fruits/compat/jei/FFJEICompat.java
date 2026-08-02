package snownee.fruits.compat.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Mutagen;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.fruits.compat.lychee.LycheeCompatClient;
import snownee.fruits.gadget.GadgetModule;

@JeiPlugin
public class FFJEICompat implements IModPlugin {

	public static final Identifier UID = FruitfulFun.id("main");

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (FFCommonConfig.isMutagenRecipeEnabled()) {
			NoHashBrewingRecipe brewingRecipe = new NoHashBrewingRecipe(
					BeeModule.MUTAGEN.key(),
					List.of(new ItemStack(MutagenItem.BREWING_ITEM)),
					List.of(Items.POTION.getDefaultInstance()),
					new ItemStack(BeeModule.MUTAGEN.get()),
					1);
			registration.addRecipes(RecipeTypes.BREWING, List.of(brewingRecipe));
		}

		LycheeCompatClient.addInformation(registration::addItemStackInfo);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		if (FFCommonConfig.isMutagenRecipeEnabled()) {
			IRecipeLookup<IJeiBrewingRecipe> recipeLookup = jeiRuntime.getRecipeManager().createRecipeLookup(RecipeTypes.BREWING);
			List<IJeiBrewingRecipe> recipes = recipeLookup.get().filter($ -> !$.getPotionOutput()
					.getOrDefault(BeeModule.MUTAGEN_CONTENT.get(), Mutagen.IMPERFECT)
					.isImperfect()).toList();
			jeiRuntime.getRecipeManager().hideRecipes(RecipeTypes.BREWING, recipes);
		}
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		if (Hooks.gadget) {
			List.of(
							GadgetModule.BUZZY_SHIELD,
							GadgetModule.ENDER_CANDLE,
							GadgetModule.PHANTOM_CANDLE,
							GadgetModule.WEAK_CANDLE,
							GadgetModule.WANDERING_TRADER_CANDLE,
							GadgetModule.PEACE_CANDLE)
					.forEach((ItemLike item) ->
							registration.registerFromDataComponentTypes(item.asItem(), GadgetModule.BUZZY_POWER_STORAGE.get()));
		}
		if (Hooks.bee) {
			registration.registerFromDataComponentTypes(BeeModule.MUTAGEN.get(), BeeModule.MUTAGEN_CONTENT.get());
		}
	}
}
