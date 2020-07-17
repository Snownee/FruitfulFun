package snownee.fruits.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import snownee.fruits.Fruits;
import snownee.fruits.Hook;
import snownee.fruits.hybridization.Hybridization;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation(Fruits.MODID, "hybriding");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (Fruits.mixin && Registry.RECIPE_TYPE.containsKey(UID)) {
            registration.addRecipeCategories(new HybridingCategory(registration.getJeiHelpers().getGuiHelper()));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Fruits.mixin && Registry.RECIPE_TYPE.containsKey(UID)) {
            ClientWorld world = Minecraft.getInstance().world;
            RecipeManager recipeManager = world.getRecipeManager();
            registration.addRecipes(recipeManager.getRecipes(Hybridization.RECIPE_TYPE).values(), UID);
        }
    }
}
