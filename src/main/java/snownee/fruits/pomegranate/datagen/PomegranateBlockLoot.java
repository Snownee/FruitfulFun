package snownee.fruits.pomegranate.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snownee.fruits.datagen.CoreBlockLoot;
import snownee.kiwi.util.Util;

public class PomegranateBlockLoot extends CoreBlockLoot {
	public PomegranateBlockLoot(FabricDataOutput dataOutput) {
		super(Util.RL("fruitfulfun:pomegranate"), dataOutput);
	}

	@Override
	protected void addTables() {
		super.addTables();
	}
}
