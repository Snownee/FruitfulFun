package snownee.fruits.mixin.bee;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;

@Mixin(EquipmentAssetManager.class)
public class EquipmentAssetManagerMixin {
	@Shadow
	@Mutable
	private Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets;

	@Shadow
	@Final
	public static EquipmentClientInfo MISSING;

	@Inject(
			method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
			at = @At("RETURN"))
	private void apply(
			Map<Identifier, EquipmentClientInfo> preparations,
			ResourceManager manager,
			ProfilerFiller profiler,
			CallbackInfo ci) {
		if (!Hooks.bee) {
			return;
		}
		EquipmentClientInfo info = equipmentAssets.getOrDefault(EquipmentAssets.SADDLE, MISSING);
		ImmutableMap.Builder<EquipmentClientInfo.LayerType, List<EquipmentClientInfo.Layer>> layers = ImmutableMap.builder();
		ImmutableList.Builder<EquipmentClientInfo.Layer> list = ImmutableList.builder();
		ImmutableMap.Builder<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = ImmutableMap.builder();
		list.addAll(info.layers().getOrDefault(EquipmentClientInfo.LayerType.FRUITFULFUN_BEE_SADDLE, List.of()));
		list.add(new EquipmentClientInfo.Layer(FruitfulFun.id("saddle")));
		layers.putAll(info.layers());
		layers.put(EquipmentClientInfo.LayerType.FRUITFULFUN_BEE_SADDLE, list.build());
		equipmentAssets.putAll(this.equipmentAssets);
		equipmentAssets.put(EquipmentAssets.SADDLE, new EquipmentClientInfo(layers.buildKeepingLast()));
		this.equipmentAssets = equipmentAssets.buildKeepingLast();
	}
}
