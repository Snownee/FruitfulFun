package snownee.fruits.pomegranate.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snownee.fruits.datagen.CoreBlockLoot;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.fruits.pomegranate.block.HangingFruitLeavesBlock;
import snownee.kiwi.util.Util;

public class PomegranateBlockLoot extends CoreBlockLoot {
	public PomegranateBlockLoot(FabricDataOutput dataOutput) {
		super(Util.RL("fruitfulfun:pomegranate"), dataOutput);
	}

	@Override
	protected void addTables() {
		super.addTables();
		HangingFruitLeavesBlock leaves = PomegranateModule.POMEGRANATE_LEAVES.get();
		add(leaves, createLeavesDrops(leaves, PomegranateModule.POMEGRANATE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
	}
}
