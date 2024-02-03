package snownee.fruits;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.levelgen.foliageplacers.Fruitify;
import snownee.fruits.pomegranate.PomegranateFruitTypes;

public class CoreFruitType extends FruitType {

	public ResourceKey<ConfiguredFeature<?, ?>> treeFeature;
	public ResourceKey<ConfiguredFeature<?, ?>> treeBeesFeature;
	public ResourceKey<ConfiguredFeature<?, ?>> treeFancyFeature;

	public CoreFruitType(int tier, Supplier<Block> log, Supplier<? extends FruitLeavesBlock> leaves, Supplier<? extends Block> sapling, Supplier<Item> fruit) {
		super(tier, log, leaves, sapling, fruit);
	}

	@Override
	public void receiveKey(ResourceLocation id) {
		treeFeature = FeatureUtils.createKey(id.toString());
		treeFancyFeature = FeatureUtils.createKey(id.withSuffix("_fancy").toString());
		treeBeesFeature = FeatureUtils.createKey(id.withSuffix("_bees").toString());
	}

	@Override
	public void makeFeatures(ResourceLocation id, boolean worldgen, BiConsumer<ResourceLocation, TreeConfiguration> exporter) {
		exporter.accept(id, treeBuilder(false, worldgen).build());
		exporter.accept(id.withSuffix("_fancy"), treeBuilder(true, worldgen).build());
		exporter.accept(id.withSuffix("_bees"), treeBuilder(false, worldgen).decorators(List.of(new BeehiveDecorator(0.05f))).build());
	}

	protected TreeConfiguration.TreeConfigurationBuilder treeBuilder(boolean fancy, boolean worldgen) {
		FoliagePlacer foliagePlacer;
		if (fancy) {
			foliagePlacer = new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 3);
		} else {
			foliagePlacer = new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3);
		}
		TrunkPlacer trunkPlacer;
		if (PomegranateFruitTypes.POMEGRANATE.is(this)) {
			trunkPlacer = new StraightTrunkPlacer(5, 2, 0);
		} else {
			trunkPlacer = new StraightTrunkPlacer(4, 2, 0);
		}
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(log.get()),
				trunkPlacer,
				BlockStateProvider.simple(leaves.get()),
				new Fruitify(foliagePlacer, worldgen),
				new TwoLayersFeatureSize(1, 0, 1)
		).ignoreVines();
	}

	@Override
	public ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
		if (pRandom.nextInt(5) == 0) {
			return treeFancyFeature;
		}
		return pLargeHive ? treeBeesFeature : treeFeature;
	}

//	public static Holder<ConfiguredFeature<?, ?>> makeConfiguredFeature(FruitType type, boolean worldGen, Supplier<Block> carpet) {
//		BlockStateProvider leavesProvider;
//		List<TreeDecorator> decorators;
//		if (worldGen) {
//			if (carpet == null) {
//				decorators = ImmutableList.of(new BeehiveDecorator(0.05F));
//			} else {
//				decorators = ImmutableList.of(new BeehiveDecorator(0.05F), new CarpetTreeDecorator(BlockStateProvider.simple(carpet.get())));
//			}
//			leavesProvider = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(type.leaves.get().defaultBlockState(), 2).add(type.leaves.get().defaultBlockState().setValue(FruitLeavesBlock.AGE, 2), 1));
//		} else {
//			decorators = ImmutableList.of();
//			leavesProvider = BlockStateProvider.simple(type.leaves.get());
//		}
//		StringBuffer buf = new StringBuffer(FFRegistries.FRUIT_TYPE.getKey(type).toString());
//		if (worldGen) {
//			buf.append("_wg");
//		}
//		/* off */
//		return FeatureUtils.register(buf.toString(), Feature.TREE,
//				new TreeConfigurationBuilder(
//						BlockStateProvider.simple(type.log.get()),
//						new StraightTrunkPlacer(4, 2, 0),
//						leavesProvider,
//						new FruitBlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
//						new TwoLayersFeatureSize(1, 0, 1)
//				)
//						.ignoreVines()
//						.decorators(decorators)
//						.build()
//		);
//		/* on */
//	}

}
