package snownee.fruits;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import snownee.fruits.block.FruitLeavesBlock;

public abstract class FruitType {

	public final int tier;
	public final Supplier<? extends Block> log;
	public final Supplier<? extends FruitLeavesBlock> leaves;
	public final Supplier<? extends Block> sapling;
	public final Supplier<Item> fruit;
	public Holder<PoiType> poiType;

	public FruitType(int tier, Supplier<Block> log, Supplier<? extends FruitLeavesBlock> leaves, Supplier<? extends Block> sapling, Supplier<Item> fruit) {
		this.tier = tier;
		this.log = log;
		this.leaves = leaves;
		this.sapling = sapling;
		this.fruit = fruit;
	}

	public static Item getFruitOrDefault(String id) {
		Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(id));
		if (block instanceof FruitLeavesBlock leavesBlock) {
			return leavesBlock.type.get().fruit.get();
		} else {
			return block.asItem();
		}
	}

	public abstract void receiveKey(ResourceLocation id);

	public abstract void makeFeatures(ResourceLocation id, boolean worldgen, BiConsumer<ResourceLocation, TreeConfiguration> exporter);

	public abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive);
}
