package snownee.fruits.gadget.vac;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.RandomSource;

public class ItemProjectileRenderer extends EntityRenderer<VacItemProjectile, ItemEntityRenderState> {
	private final ItemModelResolver itemModelResolver;
	private final RandomSource random = RandomSource.create();

	public ItemProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemModelResolver = context.getItemModelResolver();
		this.shadowRadius = 0.15f;
		this.shadowStrength = 0.75f;
	}

	@Override
	public ItemEntityRenderState createRenderState() {
		return null;
	}

//	@Override
//	public void render(VacItemProjectile itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
//		float t;
//		float s;
//		poseStack.pushPose();
//		ItemStack itemStack = itemEntity.getItem();
//		int j = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
//		this.random.setSeed(j);
//		BakedModel bakedModel = this.itemModelResolver.getModel(itemStack, itemEntity.level(), null, itemEntity.getId());
//		boolean bl = bakedModel.isGui3d();
//		int k = this.getRenderAmount(itemStack);
//		float l = Mth.sin(((float) itemEntity.getAge() + g) / 10.0f) * 0.1f + 0.1f;
//		float m = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
//		poseStack.translate(0.0f, l + 0.25f * m, 0.0f);
//		float n = itemEntity.getSpin(g);
//		poseStack.mulPose(Axis.YP.rotation(n));
//		float o = bakedModel.getTransforms().ground.scale.x();
//		float p = bakedModel.getTransforms().ground.scale.y();
//		float q = bakedModel.getTransforms().ground.scale.z();
//		if (!bl) {
//			float r = -0.0f * (float) (k - 1) * 0.5f * o;
//			s = -0.0f * (float) (k - 1) * 0.5f * p;
//			t = -0.09375f * (float) (k - 1) * 0.5f * q;
//			poseStack.translate(r, s, t);
//		}
//		for (int u = 0; u < k; ++u) {
//			poseStack.pushPose();
//			if (u > 0) {
//				if (bl) {
//					s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
//					t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
//					float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
//					poseStack.translate(s, t, v);
//				} else {
//					s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
//					t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
//					poseStack.translate(s, t, 0.0f);
//				}
//			}
//			this.itemModelResolver.render(
//					itemStack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY, bakedModel);
//			poseStack.popPose();
//			if (bl) {
//				continue;
//			}
//			poseStack.translate(0.0f * o, 0.0f * p, 0.09375f * q);
//		}
//		poseStack.popPose();
//		super.render(itemEntity, f, g, poseStack, multiBufferSource, i);
//	}
}
