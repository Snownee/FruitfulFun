package snownee.fruits;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.kiwi.KiwiGO;

public class FruitType extends ForgeRegistryEntry<FruitType> {

	public static IForgeRegistry<FruitType> REGISTRY;

	public final int tier;
	public final Supplier<? extends Block> log;
	public final KiwiGO<? extends FruitLeavesBlock> leaves;
	public final Supplier<SaplingBlock> sapling;
	public final Supplier<Item> fruit;
	@Nullable
	public final Supplier<Block> carpet;
	public Holder<ConfiguredFeature<TreeConfiguration, ?>> feature;
	@Nullable
	public Holder<ConfiguredFeature<TreeConfiguration, ?>> featureWG;

	public FruitType(int tier, Supplier<? extends Block> log, KiwiGO<? extends FruitLeavesBlock> leaves, Supplier<SaplingBlock> sapling, Supplier<Item> fruit) {
		this(tier, log, leaves, sapling, fruit, null);
	}

	public FruitType(int tier, Supplier<? extends Block> log, KiwiGO<? extends FruitLeavesBlock> leaves, Supplier<SaplingBlock> sapling, Supplier<Item> fruit, @Nullable Supplier<Block> carpet) {
		this.tier = tier;
		this.log = log;
		this.leaves = leaves;
		this.sapling = sapling;
		this.fruit = fruit;
		this.carpet = carpet;
	}

	void makeFeature() {
		feature = CoreModule.buildTreeFeature(this, false, null);
		if (tier == 0)
			featureWG = CoreModule.buildTreeFeature(this, true, carpet);
	}

}
