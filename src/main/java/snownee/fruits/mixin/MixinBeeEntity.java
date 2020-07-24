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
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
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
                if (!((BeeEntity) entity).pollinateGoal.func_226503_k_()) {
                    super.tick();
                }
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.WITHER || super.isInvulnerableTo(source);
    }

}
