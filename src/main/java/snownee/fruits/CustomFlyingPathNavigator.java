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

public class CustomFlyingPathNavigator extends FlyingPathNavigator {

    public CustomFlyingPathNavigator(MobEntity entityIn, World worldIn) {
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
        this.nodeProcessor = new FlyingNodeProcessor() {
            @Override
            protected PathNodeType func_215744_a(IBlockReader world, boolean p_215744_2_, boolean p_215744_3_, BlockPos pos, PathNodeType nodeType) {
                return nodeType == PathNodeType.LEAVES ? PathNodeType.OPEN : super.func_215744_a(world, p_215744_2_, p_215744_3_, pos, nodeType);
            }
        };
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }
}
