package snownee.fruits.minigame.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import snownee.fruits.FruitfulFun;

public final class MinigameLootTables extends SimpleFabricLootTableSubProvider {
	public MinigameLootTables(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup, LootContextParamSets.EMPTY);
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		output.accept(
				ResourceKey.create(Registries.LOOT_TABLE, FruitfulFun.id("minigame/lootbox1")),
				LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.EMERALD))));
	}
}
