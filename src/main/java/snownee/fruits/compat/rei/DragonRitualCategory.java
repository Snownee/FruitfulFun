package snownee.fruits.compat.rei;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.compat.FFJEIREI;
import snownee.fruits.food.FoodModule;
import snownee.fruits.ritual.DragonRitualContext;
import snownee.fruits.ritual.DragonRitualRecipe;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.compat.rei.category.ItemAndBlockBaseCategory;
import snownee.lychee.core.recipe.type.LycheeRecipeType;

public class DragonRitualCategory extends ItemAndBlockBaseCategory<DragonRitualContext, DragonRitualRecipe, DragonRitualDisplay> {
	public DragonRitualCategory(LycheeRecipeType<DragonRitualContext, DragonRitualRecipe> recipeType) {
		super(List.of(recipeType), GuiGameElement.of(Items.DRAGON_HEAD));
	}

	@Override
	public BlockState getIconBlock(List<DragonRitualRecipe> recipes) {
		return FoodModule.CHORUS_FRUIT_PIE.defaultBlockState();
	}

	@Override
	public BlockState getRenderingBlock(DragonRitualRecipe recipe) {
		return FoodModule.CHORUS_FRUIT_PIE.defaultBlockState();
	}

	@Override
	public @Nullable BlockPredicate getInputBlock(DragonRitualRecipe recipe) {
		return FFJEIREI.pieBlockPredicate.get();
	}

	@Override
	public int getRealWidth() {
		return super.getRealWidth() + 20;
	}

}
