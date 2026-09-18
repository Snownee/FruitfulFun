package snownee.fruits.market.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.FruitfulFun;
import snownee.fruits.market.MarketBlock;
import snownee.fruits.market.MarketModule;
import snownee.kiwi.datagen.KiwiBlockLoot;

public final class MarketBlockLoot extends KiwiBlockLoot {
	public MarketBlockLoot(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(FruitfulFun.id("market"), dataOutput, registryLookup);
	}

	@Override
	protected void addTables() {
		handle(MarketBlock.class, block -> createSingleItemTable(block).apply(
				CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
						.include(MarketModule.MARKET_MONEY.get())
						.include(MarketModule.MARKET_ORDERS.get())));
		handleDefault(this::createSingleItemTable);
	}
}
