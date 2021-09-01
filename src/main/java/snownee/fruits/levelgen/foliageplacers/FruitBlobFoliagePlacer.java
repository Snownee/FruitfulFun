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
			this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, pAttachment.pos(), j, i, pAttachment.doubleTrunk());
			BlockState core = pConfig.foliageProvider.getState(pRandom, pAttachment.pos());
			if (core.getBlock() instanceof FruitLeavesBlock) {
				core = core.setValue(LeavesBlock.DISTANCE, 1).setValue(LeavesBlock.PERSISTENT, true);
			}
			if (TreeFeature.validTreePos(pLevel, pAttachment.pos())) {
				pBlockSetter.accept(pAttachment.pos(), core);
			}
		}
	}

}
