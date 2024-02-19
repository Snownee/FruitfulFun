package snownee.fruits.pomegranate.block;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;

public class HangingFruitLeavesBlock extends FruitLeavesBlock {
	public HangingFruitLeavesBlock(Supplier<FruitType> type, Properties properties) {
		super(type, properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult ray) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean hasFruit(BlockState state, Level level, BlockPos pos) {
		return state.getValue(AGE) == 3 && level.getBlockState(pos.below()).getBlock().asItem() == type.get().fruit.get();
	}

	@Override
	public @Nullable ItemEntity doDropFruit(
			ServerLevel level,
			BlockPos pos,
			BlockState state,
			@Nullable FruitTreeBlockEntity core,
			int consumeLifespan) {
		BlockPos below = pos.below();
		level.removeBlock(below, false);
		return createItemEntity(level, below, type.get().fruit.get().getDefaultInstance());
	}

	@Override
	public boolean isPathfindable(
			BlockState blockState,
			BlockGetter blockGetter,
			BlockPos blockPos,
			PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		int age = state.getValue(AGE);
		if (age == 3) {
			age = 1;
		} else if (age == 2) {
			FruitTreeBlockEntity core = findCore(world, pos);
			if (core == null || core.isDead()) {
				age = 0;
				state = state.setValue(PERSISTENT, false);
			} else {
				age++;
			}
		} else {
			age++;
		}
		world.setBlockAndUpdate(pos, state.setValue(AGE, age));
		if (age == 3) {
			BlockPos below = pos.below();
			if (world.getBlockState(below).canBeReplaced()) {
				Block block = Block.byItem(type.get().fruit.get());
				world.setBlockAndUpdate(below, block.defaultBlockState());
			}
		}
	}
}
