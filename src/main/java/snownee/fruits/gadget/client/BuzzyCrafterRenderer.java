package snownee.fruits.gadget.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.gadget.BuzzyCrafterBlockEntity;

public final class BuzzyCrafterRenderer implements BlockEntityRenderer<BuzzyCrafterBlockEntity, BuzzyCrafterRenderState> {
	private final ItemModelResolver itemRenderer;
	private final RandomSource random = RandomSource.create();

	public BuzzyCrafterRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.itemModelResolver();
	}

//	@Override
//	public void render(
//			BuzzyCrafterBlockEntity pBlockEntity,
//			float pPartialTick,
//			PoseStack pPoseStack,
//			MultiBufferSource pBufferSource,
//			int pPackedLight,
//			int pPackedOverlay) {
//		// borrowed from ItemEntityRenderer
//
//		var itemstack = pBlockEntity.getFirstItem();
//		if (itemstack.isEmpty()) {
//			return;
//		}
//
//		var speed = 1;
//		var pos = pBlockEntity.getBlockPos();
//		Level level = Objects.requireNonNull(pBlockEntity.getLevel());
//		float spin = level.getGameTime();
//		spin += pPartialTick;
//		spin *= 0.05F;
//
//		this.random.setSeed(itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue());
//
//		pPoseStack.pushPose();
//		var bakedmodel = this.itemRenderer.getModel(itemstack, pBlockEntity.getLevel(), null, speed);
//		var gui3d = bakedmodel.isGui3d();
//		var amount = this.getRenderAmount(itemstack);
//		var modelScale = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
//
//		BlockPos above = pos.above();
//		float yOff = level.getBlockState(above).canBeReplaced() ? 0.1f : 0.5f;
//		pPoseStack.translate(0.5, 1 + yOff + 0.25 * modelScale, 0.5);
//		pPoseStack.mulPose(Axis.YP.rotation(spin));
//
//		if (!gui3d) {
//			pPoseStack.translate(
//					-0.0F * (float) (amount - 1) * 0.5F,
//					-0.0F * (float) (amount - 1) * 0.5F,
//					-0.09375F * (float) (amount - 1) * 0.5F);
//		}
//
//
//		for (var k = 0; k < amount; ++k) {
//			pPoseStack.pushPose();
//			if (k > 0) {
//				if (gui3d) {
//					pPoseStack.translate(
//							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
//							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
//							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F);
//				} else {
//					pPoseStack.translate(
//							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
//							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
//							0.0D);
//				}
//			}
//
//			var packedLight = LightTexture.pack(
//					level.getBrightness(LightLayer.BLOCK, above),
//					level.getBrightness(LightLayer.SKY, above));
//			this.itemRenderer.render(
//					itemstack,
//					ItemDisplayContext.GROUND,
//					false,
//					pPoseStack,
//					pBufferSource,
//					packedLight,
//					OverlayTexture.NO_OVERLAY,
//					bakedmodel);
//			pPoseStack.popPose();
//			if (!gui3d) {
//				pPoseStack.translate(0.0, 0.0, 0.09375F);
//			}
//		}
//
//		pPoseStack.popPose();
//	}

	private int getRenderAmount(ItemStack pStack) {
		var i = 1;
		if (pStack.getCount() > 48) {
			i = 5;
		} else if (pStack.getCount() > 32) {
			i = 4;
		} else if (pStack.getCount() > 16) {
			i = 3;
		} else if (pStack.getCount() > 1) {
			i = 2;
		}

		return i;
	}

	@Override
	public BuzzyCrafterRenderState createRenderState() {
		return null;
	}

	@Override
	public void submit(
			BuzzyCrafterRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {

	}
}