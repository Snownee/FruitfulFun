package snownee.fruits.cherry.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import snownee.fruits.cherry.block.SlidingDoorEntity;

public class SlidingDoorRenderer extends EntityRenderer<SlidingDoorEntity> {

	public SlidingDoorRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(SlidingDoorEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
	}

	@Override
	public boolean shouldRender(SlidingDoorEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		return false;
	}

	@Override
	public ResourceLocation getEntityTexture(SlidingDoorEntity entity) {
		return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
	}

}
