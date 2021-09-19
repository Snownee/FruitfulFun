package snownee.fruits.data;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.kiwi.data.provider.KiwiBlockLoot;
import snownee.kiwi.util.Util;

public class CoreBlockLoot extends KiwiBlockLoot {

	public CoreBlockLoot() {
		this(Util.RL("fruittrees:core"));
	}

	public CoreBlockLoot(ResourceLocation moduleId) {
		super(moduleId);
	}

	@Override
	protected void _addTables() {
		handleDefault($ -> BlockLoot.createSingleItemTable($));
		handle(DoorBlock.class, $ -> BlockLoot.createDoorTable($));
		handle(SlabBlock.class, $ -> BlockLoot.createSlabItemTable($));
		handle(FlowerPotBlock.class, $ -> BlockLoot.createPotFlowerItemTable(((FlowerPotBlock) $).getContent()));
		handle(FruitLeavesBlock.class, CoreBlockLoot::createFruitLeaves);
	}

	private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[] { 0.05F, 0.0625F, 0.083333336F, 0.1F };

	public static LootTable.Builder createFruitLeaves(Block block) {
		FruitLeavesBlock leavesBlock = (FruitLeavesBlock) block;
		FruitType type = leavesBlock.type.get();
		LootTable.Builder loot = createLeavesDrops(leavesBlock, type.sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES);

		LootPool.Builder pool = LootPool.lootPool();
		pool.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FruitLeavesBlock.AGE, 3))).add(LootItem.lootTableItem(type.fruit));
		loot.withPool(pool);

		return loot;
	}

}
