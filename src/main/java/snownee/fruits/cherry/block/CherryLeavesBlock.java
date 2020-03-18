package snownee.fruits.cherry.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import snownee.fruits.Fruits.Type;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.world.gen.treedecorator.CarpetTreeDecorator;

public class CherryLeavesBlock extends FruitLeavesBlock {

    public CherryLeavesBlock(Supplier<Type> type, Properties properties) {
        super(type, properties);
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
}
