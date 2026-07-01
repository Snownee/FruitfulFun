package snownee.fruits.compat.lychee;

import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.food.FoodModule;
import snownee.fruits.ritual.DragonRitualRecipe;
import snownee.fruits.ritual.RitualModule;
import snownee.lychee.compat.recipeviewer.category.DecorationMapBuilder;
import snownee.lychee.compat.recipeviewer.category.ItemAndBlockCategory;

public class DragonRitualCategory extends ItemAndBlockCategory<DragonRitualRecipe> {

	public DragonRitualCategory() {
		super(RitualModule.RECIPE_TYPE.get());
	}

	@Override
	public BlockState getRenderingBlock(DragonRitualRecipe recipe) {
		return FoodModule.CHORUS_FRUIT_PIE.defaultBlockState();
	}

	@Override
	public void setupDecorations(DecorationMapBuilder<DragonRitualRecipe> mapBuilder) {
		super.setupDecorations(mapBuilder);
		mapBuilder.condition("consume_block_in", _ -> false);
	}
}
