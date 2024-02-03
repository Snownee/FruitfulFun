package snownee.fruits.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
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
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.kiwi.datagen.KiwiBlockLoot;
import snownee.kiwi.util.Util;

public class CoreBlockLoot extends KiwiBlockLoot {

	public CoreBlockLoot(FabricDataOutput dataOutput) {
		this(Util.RL("fruitfulfun:core"), dataOutput);
	}

	public CoreBlockLoot(ResourceLocation moduleId, FabricDataOutput dataOutput) {
		super(moduleId, dataOutput);
	}

	@Override
	protected void addTables() {
		handleDefault(this::createSingleItemTable);
		handle(DoorBlock.class, this::createDoorTable);
		handle(SlabBlock.class, this::createSlabItemTable);
		handle(FlowerPotBlock.class, $ -> createPotFlowerItemTable($.getContent()));
		handle(FruitLeavesBlock.class, this::createFruitLeaves);
	}

	protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

	public LootTable.Builder createFruitLeaves(FruitLeavesBlock block) {
		FruitType type = block.type.get();
		Block dropBlock = block;
		if (CherryModule.CHERRY_LEAVES.is(block)) {
			dropBlock = Blocks.CHERRY_LEAVES;
		}
		LootTable.Builder loot = createLeavesDrops(dropBlock, type.sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES);

		LootPool.Builder pool = LootPool.lootPool();
		pool.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
				.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FruitLeavesBlock.AGE, 3)))
				.add(LootItem.lootTableItem(type.fruit.get()));
		loot.withPool(pool);

		return loot;
	}
}
