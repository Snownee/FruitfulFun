package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.kiwi.datagen.KiwiBlockLoot;

public class CoreBlockLoot extends KiwiBlockLoot {

	public CoreBlockLoot(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		this(FruitfulFun.id("core"), dataOutput, registryLookup);
	}

	public CoreBlockLoot(Identifier moduleId, FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(moduleId, dataOutput, registryLookup);
	}

	@Override
	protected void addTables() {
		handleDefault(this::createSingleItemTable);
		handle(DoorBlock.class, this::createDoorTable);
		handle(SlabBlock.class, this::createSlabItemTable);
		handle(FlowerPotBlock.class, $ -> createPotFlowerItemTable($.getPotted()));
		handle(FruitLeavesBlock.class, this::createFruitLeaves);
	}

	protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

	public LootTable.Builder createFruitLeaves(FruitLeavesBlock block) {
		FruitType type = block.type.value();
		Block dropBlock = block;
		if (CherryModule.CHERRY_LEAVES.is(block)) {
			dropBlock = Blocks.CHERRY_LEAVES;
		}
		LootTable.Builder loot = createLeavesDrops(dropBlock, type.sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES);

		LootPool.Builder pool = LootPool.lootPool();
		pool.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FruitLeavesBlock.AGE, FruitLeavesBlock.FRUITING)))
				.add(LootItem.lootTableItem(type.fruit.get()));
		loot.withPool(pool);

		return loot;
	}
}
