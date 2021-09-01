package snownee.fruits.cherry.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import snownee.fruits.cherry.block.SlidingDoorEntity;

public class SlidingDoorRenderer extends EntityRenderer<SlidingDoorEntity> {

	public SlidingDoorRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	public void render(SlidingDoorEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
	}

	@Override
	public boolean shouldRender(SlidingDoorEntity livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return entityRenderDispatcher.shouldRenderHitBoxes();
	}

	@Override
	public ResourceLocation getTextureLocation(SlidingDoorEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

}
