package snownee.fruits;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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

public class CoreFruitType extends FruitType {
	public CoreFruitType(
			int tier,
			Supplier<Block> log,
			Supplier<? extends FruitLeavesBlock> leaves,
			Supplier<? extends Block> sapling,
			Supplier<Item> fruit) {
		super(tier, log, leaves, sapling, fruit);
	}

	@Override
	public void makeFeatures(Identifier id, boolean worldgen, BiConsumer<Identifier, TreeConfiguration> exporter) {
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
		if (FFFruitTypes.POMEGRANATE.is(this)) {
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
}
