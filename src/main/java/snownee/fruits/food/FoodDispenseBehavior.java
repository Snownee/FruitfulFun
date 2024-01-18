package snownee.fruits.food;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;

public class FoodDispenseBehavior extends DefaultDispenseItemBehavior {
	@Override
	protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
		ServerLevel level = blockSource.getLevel();
		Direction facing = blockSource.getBlockState().getValue(DispenserBlock.FACING);
		BlockPos blockPos = blockSource.getPos().relative(facing);
		DirectionalPlaceContext context = new DirectionalPlaceContext(level, blockPos, facing, itemStack, Direction.DOWN);
		BlockItem blockItem = (BlockItem) itemStack.getItem();
		if (blockItem.place(context) == InteractionResult.FAIL) {
			return super.execute(blockSource, itemStack);
		}
		return itemStack;
	}
}
