package snownee.fruits.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.fruits.block.entity.SlidingDoorEntity;

public class SlidingDoorRenderer extends EntityRenderer<SlidingDoorEntity> {

	public SlidingDoorRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(
			SlidingDoorEntity entity,
			float entityYaw,
			float partialTicks,
			PoseStack poseStack,
			MultiBufferSource buffer,
			int packedLight) {
		BlockPos doorPos = entity.doorPos();
		if (doorPos == null || !(entity.bottomState.getBlock() instanceof SlidingDoorBlock)) {
			return;
		}

		@SuppressWarnings("deprecation")
		long seed = Mth.getSeed(doorPos.getX(), 0, doorPos.getZ());
		float zFightingOffset = 0.002F;
		double y = ((float) (seed >> 4 & 15L) / 15.0F - 1.0) * zFightingOffset;
		double x = Mth.clamp(((float) (seed & 15L) / 15.0F - 0.5) * 0.5, -zFightingOffset, zFightingOffset);
		double z = Mth.clamp(((float) (seed >> 8 & 15L) / 15.0F - 0.5) * 0.5, -zFightingOffset, zFightingOffset);
		AABB bounds = SlidingDoorBlock.getActualShape(entity.bottomState).bounds();
		double centerX = (bounds.minX + bounds.maxX) * 0.5D;
		double centerZ = (bounds.minZ + bounds.maxZ) * 0.5D;

		poseStack.pushPose();
		poseStack.translate(x - centerX, y, z - centerZ);

		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		int lightBottom = LevelRenderer.getLightColor(entity.level(), doorPos);
		int lightTop = LevelRenderer.getLightColor(entity.level(), doorPos.above());

		dispatcher.renderSingleBlock(entity.bottomState, poseStack, buffer, lightBottom, OverlayTexture.NO_OVERLAY);
		poseStack.translate(0, 1, 0);
		dispatcher.renderSingleBlock(entity.topState, poseStack, buffer, lightTop, OverlayTexture.NO_OVERLAY);

		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(SlidingDoorEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
