//package snownee.fruits.food.datagen;
//
//import java.util.Objects;
//
//import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//import net.minecraft.advancements.critereon.StatePropertiesPredicate;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.storage.loot.LootPool;
//import net.minecraft.world.level.storage.loot.LootTable;
//import net.minecraft.world.level.storage.loot.entries.EntryGroup;
//import net.minecraft.world.level.storage.loot.entries.LootItem;
//import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
//import snownee.fruits.food.FeastBlock;
//import snownee.kiwi.datagen.KiwiBlockLoot;
//import snownee.kiwi.util.Util;
//
//public class FoodBlockLoot extends KiwiBlockLoot {
//
//	public FoodBlockLoot(FabricDataOutput dataOutput) {
//		super(Util.RL("fruitfulfun:food"), dataOutput);
//	}
//
//	@Override
//	protected void addTables() {
//		handleDefault(this::createSingleItemTable);
//		/* off */
//		handle(FeastBlock.class, $ -> {
//			FeastBlock block = (FeastBlock) $;
//			return LootTable.lootTable().withPool(
//					LootPool.lootPool().add(LootItem.lootTableItem($).when(
//							LootItemBlockStatePropertyCondition.hasBlockStateProperties($).setProperties(
//									StatePropertiesPredicate.Builder.properties().hasProperty(block.getServingsProperty(), block.getMaxServings()))
//					).otherwise(EntryGroup.list(LootItem.lootTableItem(Items.BONE_MEAL), LootItem.lootTableItem(Objects.requireNonNull($.asItem().getCraftingRemainingItem())))))
//			);
//		});
//		/* on */
//	}
//
//}
