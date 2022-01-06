package snownee.fruits.levelgen.foliageplacers;

import java.util.Random;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import snownee.fruits.CoreModule;
import snownee.fruits.block.FruitLeavesBlock;

public class FruitBlobFoliagePlacer extends BlobFoliagePlacer {
	public static final Codec<FruitBlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> {
		return blobParts(instance).apply(instance, FruitBlobFoliagePlacer::new);
	});

	public FruitBlobFoliagePlacer(IntProvider p_i241995_1_, IntProvider p_i241995_2_, int p_i241995_3_) {
		super(p_i241995_1_, p_i241995_2_, p_i241995_3_);
	}

	@Override
	protected FoliagePlacerType<?> type() {
		return CoreModule.BLOB_PLACER;
	}

	@Override
	protected void createFoliage(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, Random pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
		for (int i = pOffset; i >= pOffset - pFoliageHeight; --i) {
			int j = Math.max(pFoliageRadius + pAttachment.radiusOffset() - 1 - i / 2, 0);
			placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, pAttachment.pos(), j, i, pAttachment.doubleTrunk());
			BlockState core = pConfig.foliageProvider.getState(pRandom, pAttachment.pos());
			if (core.getBlock() instanceof FruitLeavesBlock) {
				core = core.setValue(LeavesBlock.DISTANCE, 1).setValue(LeavesBlock.PERSISTENT, true);
			}
			if (validTreePos(pLevel, pAttachment.pos())) {
				pBlockSetter.accept(pAttachment.pos(), core);
			}
		}
	}

	// Here we replaced TreeFeature.validTreePos to avoid our FruitTreeBlockEntity being replaced
	@Override
	protected void placeLeavesRow(LevelSimulatedReader p_161438_, BiConsumer<BlockPos, BlockState> p_161439_, Random p_161440_, TreeConfiguration p_161441_, BlockPos p_161442_, int p_161443_, int p_161444_, boolean p_161445_) {
		int i = p_161445_ ? 1 : 0;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int j = -p_161443_; j <= p_161443_ + i; ++j) {
			for (int k = -p_161443_; k <= p_161443_ + i; ++k) {
				if (!shouldSkipLocationSigned(p_161440_, j, p_161444_, k, p_161443_, p_161445_)) {
					blockpos$mutableblockpos.setWithOffset(p_161442_, j, p_161444_, k);
					tryPlaceLeaf(p_161438_, p_161439_, p_161440_, p_161441_, blockpos$mutableblockpos);
				}
			}
		}
	}

	protected static void tryPlaceLeaf(LevelSimulatedReader p_161432_, BiConsumer<BlockPos, BlockState> p_161433_, Random p_161434_, TreeConfiguration p_161435_, BlockPos p_161436_) {
		if (validTreePos(p_161432_, p_161436_)) {
			p_161433_.accept(p_161436_, p_161435_.foliageProvider.getState(p_161434_, p_161436_));
		}
	}

	public static boolean validTreePos(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, state -> {
			return !state.hasProperty(LeavesBlock.PERSISTENT) || !state.getValue(LeavesBlock.PERSISTENT);
		}) && TreeFeature.validTreePos(level, pos);
	}

}
