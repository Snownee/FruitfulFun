package snownee.fruits.pomegranate.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.fruits.FruitfulFun;
import snownee.fruits.datagen.CoreBlockLoot;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.fruits.pomegranate.block.HangingFruitLeavesBlock;

public class PomegranateBlockLoot extends CoreBlockLoot {
	public PomegranateBlockLoot(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(FruitfulFun.id("pomegranate"), dataOutput, registryLookup);
	}

	@Override
	protected void addTables() {
		super.addTables();
		HangingFruitLeavesBlock leaves = PomegranateModule.POMEGRANATE_LEAVES.get();
		add(leaves, createLeavesDrops(leaves, PomegranateModule.POMEGRANATE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
	}
}
