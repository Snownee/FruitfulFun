package snownee.fruits.world.gen.feature;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractSmallTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import snownee.fruits.block.FruitLeavesBlock;

public class FruitTreeFeature extends AbstractSmallTreeFeature<TreeFeatureConfig> {

    public FruitTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> p_i225820_1_) {
        super(p_i225820_1_);
    }

    @Override
    public boolean func_225557_a_(IWorldGenerationReader world, Random rand, BlockPos pos, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox box, TreeFeatureConfig config) {
        int i = config.field_227371_p_ + rand.nextInt(config.field_227328_b_ + 1) + rand.nextInt(config.field_227329_c_ + 1);
        int j = config.field_227330_d_ >= 0 ? config.field_227330_d_ + rand.nextInt(config.field_227331_f_ + 1) : i - (config.field_227334_i_ + rand.nextInt(config.field_227335_j_ + 1));
        int k = config.field_227327_a_.func_225573_a_(rand, j, i, config);
        Optional<BlockPos> optional = this.func_227212_a_(world, i, j, k, pos, config);
        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPos blockpos = optional.get();
            this.setDirtAt(world, blockpos.down(), blockpos);
            config.field_227327_a_.func_225571_a_(world, rand, config, i, j, k, blockpos, p_225557_5_);
            this.func_227213_a_(world, rand, i, blockpos, config.field_227332_g_ + rand.nextInt(config.field_227333_h_ + 1), p_225557_4_, box, config);
            blockpos = blockpos.up(i);
            BlockState core = config.field_227369_n_.func_225574_a_(rand, blockpos);
            if (core.getBlock() instanceof FruitLeavesBlock) {
                core = core.with(LeavesBlock.DISTANCE, 1).with(LeavesBlock.PERSISTENT, true);
            }
            world.setBlockState(blockpos, core, 19);
            return true;
        }
    }

}
