package snownee.fruits.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FlowerCrownRenderer implements TrinketRenderer {

	@Override
	public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (!(entityModel instanceof HeadedModel headedModel)) {
			return;
		}
		poseStack.pushPose();
		headedModel.getHead().translateAndRotate(poseStack);
		boolean bl = entity instanceof Villager || entity instanceof ZombieVillager;
		CustomHeadLayer.translateToHead(poseStack, bl);
		if (!entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			poseStack.scale(1.18F, 1.18F, 1.18F);
		}
		ItemDisplayContext displayContext = ItemDisplayContext.HEAD;
		Minecraft.getInstance().getItemRenderer().renderStatic(entity, itemStack, displayContext, false, poseStack, multiBufferSource, entity.level(), i, OverlayTexture.NO_OVERLAY, entity.getId() + displayContext.ordinal());
		poseStack.popPose();
	}
}
