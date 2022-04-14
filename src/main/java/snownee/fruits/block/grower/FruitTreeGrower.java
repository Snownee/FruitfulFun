package snownee.fruits.block.grower;

import java.util.Random;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import snownee.fruits.FruitType;

public class FruitTreeGrower extends AbstractTreeGrower {

	private final FruitType typeSupplier;

	public FruitTreeGrower(FruitType type) {
		typeSupplier = type;
	}

	@Override
	protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(Random pRandom, boolean pLargeHive) {
		return typeSupplier.feature;
	}

}
