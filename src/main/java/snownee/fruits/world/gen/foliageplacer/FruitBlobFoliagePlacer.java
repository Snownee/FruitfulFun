package snownee.fruits.world.gen.foliageplacer;

import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import snownee.fruits.CoreModule;
import snownee.fruits.block.FruitLeavesBlock;

public class FruitBlobFoliagePlacer extends BlobFoliagePlacer {
    public static final Codec<FruitBlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> {
        return func_236740_a_(instance).apply(instance, FruitBlobFoliagePlacer::new);
    });

    public FruitBlobFoliagePlacer(int i, int j, int k, int l, int m) {
        super(i, j, k, l, m, CoreModule.BLOB_PLACER);
    }

    @Override
    protected FoliagePlacerType<?> func_230371_a_() {
        return CoreModule.BLOB_PLACER;
    }

    @Override
    protected void /*generate*/ func_230372_a_(IWorldGenerationReader world, Random random, BaseTreeFeatureConfig config, int trunkHeight, FoliagePlacer.Foliage treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int i, MutableBoundingBox blockBox) {
        for (int j = i; j >= i - foliageHeight; --j) {
            int k = Math.max(radius + treeNode./*getFoliageRadius*/func_236764_b_() - 1 - j / 2, 0);
            this./*generate*/func_236753_a_(world, random, config, treeNode./*getCenter*/func_236763_a_(), k, leaves, j, treeNode./*isGiantTrunk*/func_236765_c_(), blockBox);
            BlockState core = config.leavesProvider.getBlockState(random, treeNode./*getCenter*/func_236763_a_());
            if (core.getBlock() instanceof FruitLeavesBlock) {
                core = core.with(LeavesBlock.DISTANCE, 1).with(LeavesBlock.PERSISTENT, true);
            }
            world.setBlockState(treeNode./*getCenter*/func_236763_a_(), core, 19);
        }
    }
}
