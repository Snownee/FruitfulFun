package snownee.fruits.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class FlowerCrownRenderer implements ICurioRenderer {

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int i, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		M entityModel = renderLayerParent.getModel();
		if (!(entityModel instanceof HeadedModel headedModel)) {
			return;
		}
		LivingEntity entity = slotContext.entity();
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
