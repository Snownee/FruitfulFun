package snownee.fruits.gadget.datagen;

import java.util.List;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import snownee.fruits.FruitfulFun;
import snownee.fruits.datagen.CoreBlockLoot;
import snownee.fruits.gadget.ScentedCandleBlock;

public class GadgetBlockLoot extends CoreBlockLoot {
	public GadgetBlockLoot(FabricDataOutput dataOutput) {
		super(FruitfulFun.id("gadget"), dataOutput);
	}

	@Override
	protected void addTables() {
		super.addTables();
		handle(ScentedCandleBlock.class, this::createCandleDrops);
	}

	@Override
	public LootTable.Builder createCandleDrops(Block candleBlock) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0f))
				.add(applyExplosionDecay(
						candleBlock,
						LootItem.lootTableItem(candleBlock)
								.apply(
										List.of(2, 3, 4),
										i -> SetItemCountFunction.setCount(ConstantValue.exactly(i))
												.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(candleBlock)
														.setProperties(StatePropertiesPredicate.Builder.properties()
																.hasProperty(CandleBlock.CANDLES, i))))
								.apply(SetBuzzyPowerFunction.create()))));
	}
}
