package snownee.fruits.hybridization;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Name;

@KiwiModule("hybridization")
@KiwiModule.Optional
public class HybridizationModule extends AbstractModule {

	public HybridizationModule() {
		Hooks.hybridization = true;
	}

	@Name("hybridizing")
	public static final RecipeType<HybridizingRecipe> RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "hybridizing";
		}
	};

	@Name("hybridizing")
	public static final RecipeSerializer<HybridizingRecipe> SERIALIZER = new HybridizingRecipe.Serializer();

}
