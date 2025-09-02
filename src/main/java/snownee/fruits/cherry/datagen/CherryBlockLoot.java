package snownee.fruits.cherry.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.datagen.CoreBlockLoot;

public class CherryBlockLoot extends CoreBlockLoot {

	public CherryBlockLoot(FabricDataOutput dataOutput) {
		super(FruitfulFun.id("cherry"), dataOutput);
	}

	@Override
	protected void addTables() {
		super.addTables();
		handle(SlidingDoorBlock.class, this::createDoorTable);
		handle(CherryLeavesBlock.class, this::createFruitLeaves);
		add(CherryModule.PEACH_PINK_PETALS.get(), this::createPetalsDrops);
	}

}
