package snownee.fruits.levelgen.foliageplacers;

import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.block.FruitLeavesBlock;

public class Fruitify extends FoliagePlacer {
	public static final Codec<Fruitify> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			FoliagePlacer.CODEC.fieldOf("wrapped").forGetter($ -> $.wrapped),
			Codec.BOOL.optionalFieldOf("worldgen", false).forGetter($ -> $.worldgen)
	).apply(instance, Fruitify::new));

	private final FoliagePlacer wrapped;
	private final boolean worldgen;

	public Fruitify(FoliagePlacer wrapped, boolean worldgen) {
		super(wrapped.radius, wrapped.offset);
		this.wrapped = wrapped;
		this.worldgen = worldgen;
	}

	@Override
	protected FoliagePlacerType<?> type() {
		return CoreModule.FRUITIFY.get();
	}

	@Override
	public int foliageHeight(RandomSource randomSource, int i, TreeConfiguration treeConfiguration) {
		return wrapped.foliageHeight(randomSource, i, treeConfiguration);
	}

	@Override
	public int foliageRadius(RandomSource randomSource, int i) {
		return wrapped.foliageRadius(randomSource, i);
	}

	@Override
	public boolean shouldSkipLocation(RandomSource randomSource, int i, int j, int k, int l, boolean bl) {
		return wrapped.shouldSkipLocation(randomSource, i, j, k, l, bl);
	}

	@Override
	public void createFoliage(
			LevelSimulatedReader level,
			FoliageSetter foliageSetter,
			RandomSource pRandom,
			TreeConfiguration pConfig,
			int pMaxFreeTreeHeight,
			FoliageAttachment pAttachment,
			int pFoliageHeight,
			int pFoliageRadius,
			int pOffset) {
		Set<BlockPos> activeLeaves = Sets.newLinkedHashSet();
		FruitifiedFoliageSetter fruitifiedSetter = new FruitifiedFoliageSetter(foliageSetter, activeLeaves, pRandom, worldgen);
		wrapped.createFoliage(
				level, fruitifiedSetter, pRandom, pConfig, pMaxFreeTreeHeight, pAttachment, pFoliageHeight, pFoliageRadius, pOffset);
		BlockState core = pConfig.foliageProvider.getState(pRandom, pAttachment.pos());
		if (core.getBlock() instanceof FruitLeavesBlock) {
			core = core.setValue(LeavesBlock.DISTANCE, 1).setValue(LeavesBlock.PERSISTENT, true);
		}
		foliageSetter.set(pAttachment.pos(), core);
		level.getBlockEntity(pAttachment.pos(), CoreModule.FRUIT_TREE.get()).ifPresent(be -> {
			be.addActiveLeaves(activeLeaves);
			be.setLifespan(Mth.randomBetweenInclusive(pRandom, FFCommonConfig.fruitTreeLifespanMin, FFCommonConfig.fruitTreeLifespanMax));
		});
	}

	public static class FruitifiedFoliageSetter implements FoliageSetter {
		private final FoliageSetter wrapped;
		private final Set<BlockPos> activeLeaves;
		private final RandomSource random;
		private final boolean worldgen;

		public FruitifiedFoliageSetter(FoliageSetter wrapped, Set<BlockPos> activeLeaves, RandomSource random, boolean worldgen) {
			this.wrapped = wrapped;
			this.activeLeaves = activeLeaves;
			this.random = random;
			this.worldgen = worldgen;
		}

		@Override
		public void set(BlockPos blockPos, BlockState blockState) {
			boolean active = true;
			if (blockState.hasProperty(FruitLeavesBlock.AGE)) {
				int i = random.nextInt(4);
				if (i < (worldgen ? 3 : 2)) {
					blockState = blockState.setValue(FruitLeavesBlock.AGE, 0);
					active = false;
				} else if (worldgen && i < 4) {
					blockState = blockState.setValue(FruitLeavesBlock.AGE, 2);
				}
			}
			if (active) {
				activeLeaves.add(blockPos.immutable());
			}
			wrapped.set(blockPos, blockState);
		}

		@Override
		public boolean isSet(BlockPos blockPos) {
			return wrapped.isSet(blockPos);
		}
	}

}
