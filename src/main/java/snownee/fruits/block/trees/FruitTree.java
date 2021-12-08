package snownee.fruits.block.trees;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;

public class FruitTree extends Tree {

	private final Supplier<FruitType> typeSupplier;

	public FruitTree(Supplier<FruitType> type) {
		typeSupplier = type;
	}

	@Override
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean largeHive) {
		FruitType type = typeSupplier.get();
		return CoreModule.buildTreeFeature(type, false, null);
	}

}
