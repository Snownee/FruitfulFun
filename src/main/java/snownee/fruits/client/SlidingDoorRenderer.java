package snownee.fruits.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.block.entity.SlidingDoorEntity;

public class SlidingDoorRenderer extends EntityRenderer<SlidingDoorEntity, SlidingDoorRenderState> {
	public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	private final BlockModelResolver blockModelResolver;

	public SlidingDoorRenderer(EntityRendererProvider.Context context) {
		super(context);
		blockModelResolver = context.getBlockModelResolver();
	}

	@Override
	public SlidingDoorRenderState createRenderState() {
		return new SlidingDoorRenderState();
	}

	@Override
	public void extractRenderState(SlidingDoorEntity entity, SlidingDoorRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		BlockPos doorPos = entity.doorPos();
		if (doorPos != null) {
			Vec3 offset = entity.getLerpedOffset(partialTicks);
			@SuppressWarnings("deprecation") long seed = Mth.getSeed(doorPos.getX(), 0, doorPos.getZ());
			float zFightingOffset = 0.002F;
			double y = ((float) (seed >> 4 & 15L) / 15.0F - 1.0) * zFightingOffset;
			double x = Mth.clamp(((float) (seed & 15L) / 15.0F - 0.5) * 0.5, -zFightingOffset, zFightingOffset);
			double z = Mth.clamp(((float) (seed >> 8 & 15L) / 15.0F - 0.5) * 0.5, -zFightingOffset, zFightingOffset);
			state.x = doorPos.getX() + offset.x + x;
			state.y += y;
			state.z = doorPos.getZ() + offset.z + z;
			blockModelResolver.update(state.bottomModel, entity.bottomState, BLOCK_DISPLAY_CONTEXT);
			blockModelResolver.update(state.topModel, entity.topState, BLOCK_DISPLAY_CONTEXT);
			state.lightCoords = getPackedLightCoords(entity, doorPos);
			state.lightCoordsAbove = getPackedLightCoords(entity, doorPos.above());
		} else {
			state.bottomModel.clear();
			state.topModel.clear();
		}
	}

	public int getPackedLightCoords(SlidingDoorEntity entity, BlockPos blockPos) {
		return LightCoordsUtil.pack(getBlockLightLevel(entity, blockPos), getSkyLightLevel(entity, blockPos));
	}

	@Override
	public void submit(
			SlidingDoorRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {
		poseStack.pushPose();
		state.bottomModel.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.translate(0, 1, 0);
		state.topModel.submit(poseStack, submitNodeCollector, state.lightCoordsAbove, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}
}
