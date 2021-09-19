package snownee.fruits.cherry.data;

import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.block.SlidingDoorBlock;
import snownee.fruits.data.CoreBlockLoot;
import snownee.kiwi.util.Util;

public class CherryBlockLoot extends CoreBlockLoot {

	public CherryBlockLoot() {
		super(Util.RL("fruittrees:cherry"));
	}

	@Override
	protected void _addTables() {
		super._addTables();
		handle(SlidingDoorBlock.class, $ -> createDoorTable($));
		handle(CherryLeavesBlock.class, CoreBlockLoot::createFruitLeaves);
		handle(CarpetBlock.class, $ -> createSilkTouchOrShearsDispatchTable($, applyExplosionCondition($, LootItem.lootTableItem($))));
	}

}
