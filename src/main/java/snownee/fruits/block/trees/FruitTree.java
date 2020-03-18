package snownee.fruits.block.trees;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import snownee.fruits.Fruits;
import snownee.fruits.MainModule;

public class FruitTree extends Tree {

    private final Supplier<Fruits.Type> typeSupplier;

    public FruitTree(Supplier<Fruits.Type> type) {
        this.typeSupplier = type;
    }

    @Override // FIXME
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean p_225546_2_) {
        Fruits.Type type = typeSupplier.get();
        return MainModule.buildTreeFeature(type, false, null);
    }

}
