package snownee.fruits.pomegranate.block;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;

public class HangingFruitLeavesBlock extends FruitLeavesBlock {
	public HangingFruitLeavesBlock(Holder<FruitType> type, Properties properties) {
		super(type, 0.01F, properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean hasFruit(BlockState state, Level level, BlockPos pos) {
		return state.getValue(AGE) == FruitLeavesBlock.FRUITING &&
				level.getBlockState(pos.below()).getBlock().asItem() == type.value().fruit.get();
	}

	@Override
	public @Nullable ItemEntity doDropFruit(ServerLevel level, BlockPos pos, BlockState state) {
		BlockPos below = pos.below();
		level.removeBlock(below, false);
		return createItemEntity(level, below, type.value().fruit.get().getDefaultInstance());
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}

	@Override
	public boolean canGrowWithContext(BlockState blockState, LevelReader level, BlockPos pos) {
		if (!super.canGrowWithContext(blockState, level, pos)) {
			return false;
		}
		return blockState.getValue(AGE) != BLOOMING || level.getBlockState(pos.below()).canBeReplaced();
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		int age = state.getValue(AGE);
		if (age == FruitLeavesBlock.FRUITING) {
			gotoDeadOrYoung(world, pos, state, null);
			return;
		}
		age++;
		world.setBlockAndUpdate(pos, state.setValue(AGE, age));
		if (age == FruitLeavesBlock.FRUITING) {
			FruitTreeBlockEntity core = findCore(world, pos);
			if (core != null) {
				core.consumeLifespan(1);
			}
			BlockPos below = pos.below();
			if (world.getBlockState(below).canBeReplaced()) {
				Block block = Block.byItem(type.value().fruit.get());
				world.setBlockAndUpdate(below, block.defaultBlockState());
			}
		}
	}
}
