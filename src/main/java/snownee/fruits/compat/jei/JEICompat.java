package snownee.fruits.compat.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitsConfig;
import snownee.fruits.FruitsMod;
import snownee.fruits.hybridization.HybridizingRecipe;
import snownee.fruits.hybridization.HybridizationModule;

@JeiPlugin
public class JEICompat implements IModPlugin {

	public static final ResourceLocation UID = new ResourceLocation(FruitsMod.ID, "hybridizing");
	public static final RecipeType<HybridizingRecipe> RECIPE_TYPE = new RecipeType<>(UID, HybridizingRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		if (Registry.RECIPE_TYPE.containsKey(UID)) {
			registration.addRecipeCategories(new HybridizingCategory(registration.getJeiHelpers().getGuiHelper()));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Registry.RECIPE_TYPE.containsKey(UID)) {
			ClientLevel world = Minecraft.getInstance().level;
			RecipeManager recipeManager = world.getRecipeManager();
			registration.addRecipes(RECIPE_TYPE, List.copyOf(recipeManager.byType(HybridizationModule.RECIPE_TYPE).values()));
		}

		if (FruitsConfig.appleSaplingFromHeroOfTheVillage || FruitsConfig.villageAppleTreeWorldGen) {
			String info = "";
			if (FruitsConfig.appleSaplingFromHeroOfTheVillage) {
				info = I18n.get("gui.fruittrees.tip.appleSaplingFromHeroOfTheVillage");
			}
			if (FruitsConfig.villageAppleTreeWorldGen) {
				if (FruitsConfig.appleSaplingFromHeroOfTheVillage) {
					info += "\n";
				}
				info += I18n.get("gui.fruittrees.tip.villageAppleTreeWorldGen");
			}
			ItemStack appleSapling = CoreModule.APPLE_SAPLING.itemStack();
			registration.addIngredientInfo(appleSapling, VanillaTypes.ITEM_STACK, Component.literal(info));
		}
	}
}