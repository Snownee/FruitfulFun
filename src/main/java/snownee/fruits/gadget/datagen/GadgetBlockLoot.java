package snownee.fruits.gadget.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
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
}
