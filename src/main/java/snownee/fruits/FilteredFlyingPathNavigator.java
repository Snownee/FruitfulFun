package snownee.fruits;

import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import snownee.fruits.block.FruitLeavesBlock;

public class FilteredFlyingPathNavigator extends FlyingPathNavigator {

	public FilteredFlyingPathNavigator(MobEntity entityIn, World worldIn) {
		super(entityIn, worldIn);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canEntityStandOnPos(BlockPos pos) {
		BlockState state = world.getBlockState(pos.down());
		if (!state.isAir()) {
			return true;
		}
		state = world.getBlockState(pos);
		return state.getBlock() instanceof FruitLeavesBlock;
	}

	@Override
	public void tick() {
		if (!((BeeEntity) entity).pollinateGoal.isRunning()) {
			super.tick();
		}
	}

	@Override
	protected PathFinder getPathFinder(int p_179679_1_) {
		nodeProcessor = new FlyingNodeProcessor() {
			@Override
			protected PathNodeType refineNodeType(IBlockReader world, boolean p_215744_2_, boolean p_215744_3_, BlockPos pos, PathNodeType nodeType) {
				return nodeType == PathNodeType.LEAVES ? PathNodeType.OPEN : super.refineNodeType(world, p_215744_2_, p_215744_3_, pos, nodeType);
			}
		};
		nodeProcessor.setCanEnterDoors(true);
		return new PathFinder(nodeProcessor, p_179679_1_);
	}
}
