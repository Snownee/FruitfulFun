package snownee.fruits;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.common.IPlantable;

public enum FruitType implements IExtensibleEnum {
	MANDARIN(
			0, CoreModule.CITRUS_LOG, CoreModule.MANDARIN_LEAVES, () -> CoreModule.MANDARIN_SAPLING, CoreModule.MANDARIN
	),
	LIME(0, CoreModule.CITRUS_LOG, CoreModule.LIME_LEAVES, () -> CoreModule.LIME_SAPLING, CoreModule.LIME),
	CITRON(0, CoreModule.CITRUS_LOG, CoreModule.CITRON_LEAVES, () -> CoreModule.CITRON_SAPLING, CoreModule.CITRON),
	POMELO(1, CoreModule.CITRUS_LOG, CoreModule.POMELO_LEAVES, () -> CoreModule.POMELO_SAPLING, CoreModule.POMELO),
	ORANGE(1, CoreModule.CITRUS_LOG, CoreModule.ORANGE_LEAVES, () -> CoreModule.ORANGE_SAPLING, CoreModule.ORANGE),
	LEMON(1, CoreModule.CITRUS_LOG, CoreModule.LEMON_LEAVES, () -> CoreModule.LEMON_SAPLING, CoreModule.LEMON),
	GRAPEFRUIT(
			2, CoreModule.CITRUS_LOG, CoreModule.GRAPEFRUIT_LEAVES, () -> CoreModule.GRAPEFRUIT_SAPLING,
			CoreModule.GRAPEFRUIT
	),
	APPLE(1, Blocks.OAK_LOG, CoreModule.APPLE_LEAVES, () -> CoreModule.APPLE_SAPLING, Items.APPLE);

	public final int tier;
	public final Block log;
	public final LeavesBlock leaves;
	public final Supplier<SaplingBlock> sapling;
	public final Item fruit;
	@Nullable
	public final Block carpet;
	public ConfiguredFeature<TreeConfiguration, ?> feature;
	@Nullable
	public ConfiguredFeature<TreeConfiguration, ?> featureWG;

	FruitType(int tier, Block log, LeavesBlock leaves, Supplier<SaplingBlock> sapling, Item fruit) {
		this(tier, log, leaves, sapling, fruit, null);
	}

	FruitType(int tier, Block log, LeavesBlock leaves, Supplier<SaplingBlock> sapling, Item fruit, @Nullable Block carpet) {
		this.tier = tier;
		this.log = log;
		this.leaves = leaves;
		this.sapling = sapling;
		this.fruit = fruit;
		this.carpet = carpet;
		CoreModule.ALL_LEAVES.add(leaves);
	}

	void makeFeature() {
		feature = CoreModule.buildTreeFeature(this, false, null);
		if (tier == 0)
			featureWG = CoreModule.buildTreeFeature(this, true, carpet);
	}

	public static FruitType create(String name, int tier, Block log, LeavesBlock leaves, Supplier<IPlantable> sapling, Item fruit, @Nullable Block carpet) {
		throw new IllegalStateException("Enum not extended");
	}

	public static FruitType parse(String name) {
		try {
			return valueOf(name);
		} catch (Exception e) {
			return CITRON;
		}
	}
}
