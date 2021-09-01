package snownee.fruits.block.trees;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;

public class FruitTree extends AbstractTreeGrower {

	private final Supplier<FruitType> typeSupplier;

	public FruitTree(Supplier<FruitType> type) {
		this.typeSupplier = type;
	}

	@Override
	protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random pRandom, boolean pLargeHive) {
		FruitType type = typeSupplier.get();
		return CoreModule.buildTreeFeature(type, false, null);
	}

}
