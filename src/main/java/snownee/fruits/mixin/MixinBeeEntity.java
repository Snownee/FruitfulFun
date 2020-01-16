package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(BeeEntity.class)
public abstract class MixinBeeEntity extends AnimalEntity {

    @Shadow
    public BeeEntity.PollinateGoal field_226370_bJ_;

    public MixinBeeEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    @Overwrite
    private boolean func_226439_k_(BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return this.world.isBlockPresent(pos) && block instanceof FruitLeavesBlock || block.isIn(BlockTags.field_226149_I_);
    }

    @Override
    @Overwrite
    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
            public boolean canEntityStandOnPos(BlockPos pos) {
                BlockState state = world.getBlockState(pos.down());
                if (!state.isAir()) {
                    return true;
                }
                state = world.getBlockState(pos);
                return state.getBlock() instanceof FruitLeavesBlock;
            }

            public void tick() {
                if (!field_226370_bJ_.func_226503_k_()) {
                    super.tick();
                }
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

}
