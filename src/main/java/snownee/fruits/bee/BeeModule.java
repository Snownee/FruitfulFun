package snownee.fruits.bee;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Name;

@KiwiModule("bee")
@KiwiModule.Optional
public class BeeModule extends AbstractModule {

	@Name("hybridizing")
	public static final RecipeType<HybridizingRecipe> RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "hybridizing";
		}
	};

	@Name("hybridizing")
	public static final RecipeSerializer<HybridizingRecipe> SERIALIZER = new HybridizingRecipe.Serializer();

	public static ResourceLocation BEE_ONE_CM = new ResourceLocation(FruitfulFun.ID, "bee_one_cm");

	public static ResourceLocation BEES_BRED = new ResourceLocation(FruitfulFun.ID, "bees_bred");

	public BeeModule() {
		Hooks.bee = true;
	}
}
