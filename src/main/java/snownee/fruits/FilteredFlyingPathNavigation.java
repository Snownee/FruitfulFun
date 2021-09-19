package snownee.fruits;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import snownee.fruits.block.FruitLeavesBlock;

public class FilteredFlyingPathNavigation extends FlyingPathNavigation {

	public FilteredFlyingPathNavigation(Mob entityIn, Level worldIn) {
		super(entityIn, worldIn);
	}

	@Override
	public boolean isStableDestination(BlockPos pos) {
		BlockState state = level.getBlockState(pos.below());
		if (!state.isAir()) {
			return true;
		}
		state = level.getBlockState(pos);
		return state.getBlock() instanceof FruitLeavesBlock;
	}

	@Override
	public void tick() {
		if (!((Bee) mob).beePollinateGoal.isPollinating()) {
			super.tick();
		}
	}

	@Override
	protected PathFinder createPathFinder(int p_179679_1_) {
		nodeEvaluator = new FlyNodeEvaluator() {
			@Override
			public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
				BlockPathTypes types = super.getBlockPathType(pLevel, pX, pY, pZ);
				if (types == BlockPathTypes.LEAVES) {
					types = BlockPathTypes.OPEN;
				}
				return types;
			}
		};
		nodeEvaluator.setCanPassDoors(true);
		return new PathFinder(nodeEvaluator, p_179679_1_);
	}
}
