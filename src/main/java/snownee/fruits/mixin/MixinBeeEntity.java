package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.Hybridization;

@Mixin(BeeEntity.class)
public abstract class MixinBeeEntity extends AnimalEntity {

    public MixinBeeEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "func_226439_k_", cancellable = true)
    public void canPollinate(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Block block = world.getBlockState(pos).getBlock();
        if (Hybridization.INSTANCE != null && block instanceof FruitLeavesBlock) {
            cir.setReturnValue(true);
        }
    }

    @Override
    @Overwrite
    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
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
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

}
