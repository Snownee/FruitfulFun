package snownee.fruits.food.datagen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import snownee.fruits.FruitfulFun;
import snownee.fruits.food.FeastBlock;
import snownee.fruits.food.FoodModule;
import snownee.kiwi.datagen.KiwiBlockLoot;

public class FoodBlockLoot extends KiwiBlockLoot {

	public FoodBlockLoot(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(FruitfulFun.id("food"), dataOutput, registryLookup);
	}

	@Override
	protected void addTables() {
		handleDefault(this::createSingleItemTable);
		/* off */
		add(
				FoodModule.CHORUS_FRUIT_PIE.get(), block -> {
					FeastBlock $ = (FeastBlock) block;
					return LootTable.lootTable().withPool(
							LootPool.lootPool().add(LootItem.lootTableItem($).when(
									LootItemBlockStatePropertyCondition.hasBlockStateProperties($).setProperties(
											StatePropertiesPredicate.Builder.properties()
													.hasProperty($.getServingsProperty(), $.getMaxServings()))
							))
					);
				});

		add(
				FoodModule.LEMON_ROAST_CHICKEN_BLOCK.get(), block -> {
					FeastBlock $ = (FeastBlock) block;
					return LootTable.lootTable().withPool(
							LootPool.lootPool().add(LootItem.lootTableItem($)
									.when(
											LootItemBlockStatePropertyCondition.hasBlockStateProperties($).setProperties(
													StatePropertiesPredicate.Builder.properties()
															.hasProperty($.getServingsProperty(), $.getMaxServings()))
									)
									.otherwise(EntryGroup.list(
											LootItem.lootTableItem(Items.BONE_MEAL),
											LootItem.lootTableItem(Objects.requireNonNull($.asItem().getCraftingRemainder())
													.item()
													.value()))))
					);
				});
		/* on */
	}

}
