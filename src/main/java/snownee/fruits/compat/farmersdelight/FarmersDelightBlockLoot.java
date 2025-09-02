package snownee.fruits.compat.farmersdelight;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snownee.fruits.FruitfulFun;
import snownee.fruits.datagen.CoreBlockLoot;

public class FarmersDelightBlockLoot extends CoreBlockLoot {
	public FarmersDelightBlockLoot(FabricDataOutput dataOutput) {
		super(FruitfulFun.id("farmersdelight"), dataOutput);
	}

	@Override
	protected void addTables() {
		handleDefault(this::createSingleItemTable);
	}
}
