package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.model.EquipmentClientInfo;

@Mixin(EquipmentClientInfo.LayerType.class)
enum LayerTypeMixin {
	FRUITFULFUN_BEE_SADDLE("fruitfulfun_bee_saddle");

	@Shadow
	LayerTypeMixin(final String id) {
	}
}
