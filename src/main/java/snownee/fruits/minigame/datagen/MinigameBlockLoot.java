package snownee.fruits.minigame.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.datagen.KiwiBlockLoot;

public final class MinigameBlockLoot extends KiwiBlockLoot {
	public MinigameBlockLoot(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(FruitfulFun.id("minigame"), dataOutput, registryLookup);
	}

	@Override
	protected void addTables() {
		handleDefault(this::createSingleItemTable);
	}
}
