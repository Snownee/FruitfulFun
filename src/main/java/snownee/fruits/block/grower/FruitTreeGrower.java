package snownee.fruits.block.grower;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import snownee.fruits.FruitType;

public class FruitTreeGrower extends TreeGrower {

	private final FruitType fruitType;

	public FruitTreeGrower(FruitType fruitType) {
		this.fruitType = fruitType;
	}

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
		return fruitType.getConfiguredFeature(pRandom, pLargeHive);
	}

}
