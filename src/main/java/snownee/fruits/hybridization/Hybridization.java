package snownee.fruits.hybridization;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Name;

@KiwiModule("hybridization")
@KiwiModule.Optional
public class Hybridization extends AbstractModule {

	public Hybridization() {
		Hooks.hybridization = true;
	}

	@Name("hybriding")
	public static final RecipeType<HybridingRecipe> RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "hybriding";
		}
	};

	@Name("hybriding")
	public static final RecipeSerializer<HybridingRecipe> SERIALIZER = new HybridingRecipe.Serializer();

}
