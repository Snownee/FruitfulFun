package snownee.fruits.hybridization;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.Name;

@KiwiModule("hybridization")
@KiwiModule.Optional
public class Hybridization extends AbstractModule {

	public static Hybridization INSTANCE;

	@Name("hybriding")
	public static final IRecipeType<HybridingRecipe> RECIPE_TYPE = new IRecipeType() {
	};

	@Name("hybriding")
	public static final IRecipeSerializer<HybridingRecipe> SERIALIZER = new HybridingRecipe.Serializer();

}
