package snownee.fruits.cherry;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.levelgen.foliageplacers.Fruitify;

public class CherryFruitType extends FruitType {
	public ResourceKey<ConfiguredFeature<?, ?>> treeFeature;
	public ResourceKey<ConfiguredFeature<?, ?>> treeBeesFeature;

	public CherryFruitType(
			int tier,
			Supplier<Block> log,
			Supplier<? extends FruitLeavesBlock> leaves,
			Supplier<? extends Block> sapling,
			Supplier<Item> fruit) {
		super(tier, log, leaves, sapling, fruit);
	}

	@Override
	public void receiveKey(ResourceLocation id) {
		treeFeature = FeatureUtils.createKey(id.toString());
		treeBeesFeature = FeatureUtils.createKey(id.withSuffix("_bees").toString());
	}

	@Override
	public void makeFeatures(ResourceLocation id, boolean worldgen, BiConsumer<ResourceLocation, TreeConfiguration> exporter) {
		exporter.accept(id, treeBuilder(worldgen).build());
		exporter.accept(id.withSuffix("_bees"), treeBuilder(worldgen).decorators(List.of(new BeehiveDecorator(0.05f))).build());
	}

	protected TreeConfiguration.TreeConfigurationBuilder treeBuilder(boolean worldgen) {
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(log.get()),
				new CherryTrunkPlacer(7, 1, 0,
						new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder()
								.add(ConstantInt.of(1), 1)
								.add(ConstantInt.of(2), 1)
								.add(ConstantInt.of(3), 1)
								.build()),
						UniformInt.of(2, 4),
						UniformInt.of(-4, -3),
						UniformInt.of(-1, 0)),
				BlockStateProvider.simple(leaves.get()),
				new Fruitify(new CherryFoliagePlacer(
						ConstantInt.of(4),
						ConstantInt.of(0),
						ConstantInt.of(5),
						0.25f, 0.5f, 0.16666667f, 0.33333334f), worldgen),
				new TwoLayersFeatureSize(1, 0, 2))
				.ignoreVines();
	}

	@Override
	public ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
		return pLargeHive ? treeBeesFeature : treeFeature;
	}
}
