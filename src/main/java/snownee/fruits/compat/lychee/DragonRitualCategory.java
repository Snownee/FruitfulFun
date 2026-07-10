package snownee.fruits.compat.lychee;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.food.FoodModule;
import snownee.fruits.ritual.DragonRitualRecipe;
import snownee.fruits.ritual.RitualModule;
import snownee.lychee.compat.recipeviewer.category.DecorationMapBuilder;
import snownee.lychee.compat.recipeviewer.category.ItemAndBlockCategory;
import snownee.lychee.util.VectorExtensions;

public class DragonRitualCategory extends ItemAndBlockCategory<DragonRitualRecipe> {
	public static final Vector2fc INPUT_BLOCK_POSITION = new Vector2f(40.0F, 32.0F);
	public static final Vector2fc METHOD_POSITION = new Vector2f(INPUT_BLOCK_POSITION.x() - 4.0F, 10.0F);
	public static final Vector2fc INFO_POSITION = VectorExtensions.offset(METHOD_POSITION, 20.0F, 4.0F);

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

	@Override
	public Vector2fc inputBlockPosition() {
		return INPUT_BLOCK_POSITION;
	}

	@Override
	public Vector2fc methodPosition() {
		return METHOD_POSITION;
	}

	@Override
	public Vector2fc infoPosition(DragonRitualRecipe recipe) {
		return INFO_POSITION;
	}
}
