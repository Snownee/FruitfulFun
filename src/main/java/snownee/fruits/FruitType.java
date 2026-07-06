package snownee.fruits;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import snownee.fruits.block.FruitLeavesBlock;

public abstract class FruitType {

	public final int tier;
	public final Supplier<? extends Block> log;
	public final Supplier<? extends FruitLeavesBlock> leaves;
	public final Supplier<? extends Block> sapling;
	public final Supplier<Item> fruit;
	public @Nullable Holder<PoiType> poiType;
	public boolean allogamous;

	public FruitType(
			int tier,
			Supplier<Block> log,
			Supplier<? extends FruitLeavesBlock> leaves,
			Supplier<? extends Block> sapling,
			Supplier<Item> fruit) {
		this.tier = tier;
		this.log = log;
		this.leaves = leaves;
		this.sapling = sapling;
		this.fruit = fruit;
	}

	public static Item getFruitOrItem(String id) {
		Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(id));
		if (block instanceof FruitLeavesBlock leavesBlock) {
			return leavesBlock.type.value().fruit.get();
		} else {
			return block.asItem();
		}
	}

	public static Either<FruitType, Block> getFruitOrBlock(String id) {
		Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(id));
		if (block instanceof FruitLeavesBlock leavesBlock) {
			return Either.left(leavesBlock.type.value());
		} else {
			return Either.right(block);
		}
	}

	public FruitType allogamous() {
		allogamous = true;
		return this;
	}

	public Holder<PoiType> poiType() {
		return Objects.requireNonNull(poiType);
	}

	public abstract void makeFeatures(Identifier id, boolean worldgen, BiConsumer<Identifier, TreeConfiguration> exporter);
}
