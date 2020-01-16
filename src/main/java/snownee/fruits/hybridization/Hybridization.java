package snownee.fruits.hybridization;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import snownee.fruits.NewMethods;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiManager;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.Name;

@KiwiModule(name = "hybridization")
@KiwiModule.Optional
public class Hybridization extends AbstractModule {

    @Name("hybriding")
    public static final IRecipeType<HybridingRecipe> RECIPE_TYPE = new IRecipeType() {};

    @Name("hybriding")
    public static final IRecipeSerializer<HybridingRecipe> SERIALIZER = new HybridingRecipe.Serializer();

    @Override
    protected void preInit() {
        if (!NewMethods.mixin) {
            KiwiManager.MODULES.remove(RL("hybridization"));
        }
    }

}
