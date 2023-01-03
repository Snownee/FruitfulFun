package snownee.fruits.block.grower;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import snownee.fruits.FruitType;

public class FruitTreeGrower extends AbstractTreeGrower {

	private final Supplier<FruitType> typeSupplier;

	public FruitTreeGrower(Supplier<FruitType> typeSupplier) {
		this.typeSupplier = typeSupplier;
	}

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean largeHive) {
		// TODO Auto-generated method stub
		return typeSupplier.get().feature;
	}

}
