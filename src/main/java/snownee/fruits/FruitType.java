package snownee.fruits;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import snownee.fruits.block.FruitLeavesBlock;

public class FruitType {

	public final int tier;
	public final Supplier<? extends Block> log;
	public final Supplier<? extends FruitLeavesBlock> leaves;
	public final Supplier<? extends Block> sapling;
	public final Supplier<Item> fruit;
	public Holder<ConfiguredFeature<TreeConfiguration, ?>> feature;
	@Nullable
	public Holder<ConfiguredFeature<TreeConfiguration, ?>> featureWG;

	public FruitType(int tier, Supplier<Block> log, Supplier<? extends FruitLeavesBlock> leaves, Supplier<? extends Block> sapling, Supplier<Item> fruit) {
		this.tier = tier;
		this.log = log;
		this.leaves = leaves;
		this.sapling = sapling;
		this.fruit = fruit;
	}

	public static Item getFruitOrDefault(Block block) {
		if (block instanceof FruitLeavesBlock leavesBlock) {
			return leavesBlock.type.get().fruit.get();
		} else {
			return block.asItem();
		}
	}

	void makeFeature() {
		//FIXME
//		feature = CoreModule.makeConfiguredFeature(this, false, null);
//		if (tier == 0)
//			featureWG = CoreModule.makeConfiguredFeature(this, true, null);
	}

}
