package snownee.fruits.block.trees;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import snownee.fruits.FruitType;
import snownee.fruits.MainModule;

public class FruitTree extends Tree {

    private final Supplier<FruitType> typeSupplier;

    public FruitTree(Supplier<FruitType> type) {
        this.typeSupplier = type;
    }

    @Override // FIXME
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean p_225546_2_) {
        FruitType type = typeSupplier.get();
        return MainModule.buildTreeFeature(type, false, null);
    }

}
