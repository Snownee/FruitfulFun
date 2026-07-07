package snownee.fruits.datagen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import snownee.fruits.FruitfulFun;

// EquipmentAssetProvider
public class FFEquipmentAssets implements DataProvider {
	private final PackOutput.PathProvider pathProvider;

	public FFEquipmentAssets(PackOutput output) {
		this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = new HashMap<>();
		equipmentAssets.put(
				EquipmentAssets.SADDLE, EquipmentClientInfo.builder().addLayers(
						EquipmentClientInfo.LayerType.FRUITFULFUN_BEE_SADDLE,
						new EquipmentClientInfo.Layer(FruitfulFun.id("saddle"))
				).build());
		return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, pathProvider::json, equipmentAssets);
	}

	@Override
	public String getName() {
		return "FF Equipment Asset Provider";
	}
}
