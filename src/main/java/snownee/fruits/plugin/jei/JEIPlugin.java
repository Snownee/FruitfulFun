package snownee.fruits.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import snownee.fruits.FruitsMod;
import snownee.fruits.hybridization.Hybridization;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

	public static final ResourceLocation UID = new ResourceLocation(FruitsMod.MODID, "hybriding");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		if (Registry.RECIPE_TYPE.containsKey(UID)) {
			registration.addRecipeCategories(new HybridingCategory(registration.getJeiHelpers().getGuiHelper()));
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Registry.RECIPE_TYPE.containsKey(UID)) {
			ClientLevel world = Minecraft.getInstance().level;
			RecipeManager recipeManager = world.getRecipeManager();
			registration.addRecipes(recipeManager.byType(Hybridization.RECIPE_TYPE).values(), UID);
		}
	}
}