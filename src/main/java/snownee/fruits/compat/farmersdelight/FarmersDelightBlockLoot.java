package snownee.fruits.compat.farmersdelight;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.fruits.FruitfulFun;
import snownee.fruits.datagen.CoreBlockLoot;

public class FarmersDelightBlockLoot extends CoreBlockLoot {
	public FarmersDelightBlockLoot(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(FruitfulFun.id("farmersdelight"), dataOutput, registryLookup);
	}

	@Override
	protected void addTables() {
		handleDefault(this::createSingleItemTable);
	}
}
