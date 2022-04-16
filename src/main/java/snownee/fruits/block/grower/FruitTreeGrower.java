package snownee.fruits.block.grower;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import snownee.fruits.FruitType;

public class FruitTreeGrower extends AbstractTreeGrower {

	private final Supplier<FruitType> typeSupplier;

	public FruitTreeGrower(Supplier<FruitType> typeSupplier) {
		this.typeSupplier = typeSupplier;
	}

	@Override
	protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(Random pRandom, boolean pLargeHive) {
		return typeSupplier.get().feature;
	}

}
