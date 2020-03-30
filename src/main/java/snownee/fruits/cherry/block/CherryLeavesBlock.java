package snownee.fruits.cherry.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import snownee.fruits.Fruits.Type;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.world.gen.treedecorator.CarpetTreeDecorator;

public class CherryLeavesBlock extends FruitLeavesBlock {

    protected final IParticleData particleType;

    public CherryLeavesBlock(Supplier<Type> type, Properties properties, IParticleData particleType) {
        super(type, properties);
        this.particleType = particleType;
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.randomTick(state, world, pos, rand);
        if (!canGrow(state) && rand.nextInt(20) == 0) {
            return;
        }
        CarpetTreeDecorator.placeCarpet(world, pos, getCarpet().getDefaultState(), 3);
    }

    public Block getCarpet() {
        return this == CherryModule.REDLOVE_LEAVES ? CherryModule.REDLOVE_CARPET : CherryModule.CHERRY_CARPET;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int i = rand.nextInt(15);
        boolean raining = worldIn.isRainingAt(pos.up());
        if (raining && i == 1) {
            BlockPos blockpos = pos.down();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (!blockstate.isSolid() || !blockstate.isSolidSide(worldIn, blockpos, Direction.UP)) {
                double d0 = pos.getX() + rand.nextFloat();
                double d1 = pos.getY() - 0.05D;
                double d2 = pos.getZ() + rand.nextFloat();
                worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        } else if (i == 2 || i == 3 && raining) {
            double d0 = pos.getX() + rand.nextFloat();
            double d1 = pos.getY() + rand.nextFloat();
            double d2 = pos.getZ() + rand.nextFloat();
            worldIn.addParticle(particleType, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
