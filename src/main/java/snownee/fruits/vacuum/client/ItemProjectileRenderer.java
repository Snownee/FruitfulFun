package snownee.fruits.vacuum.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.vacuum.VacItemProjectile;

public class ItemProjectileRenderer extends EntityRenderer<VacItemProjectile> {
	private final ItemRenderer itemRenderer;
	private final RandomSource random = RandomSource.create();

	public ItemProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.shadowRadius = 0.15f;
		this.shadowStrength = 0.75f;
	}

	private int getRenderAmount(ItemStack itemStack) {
		int i = 1;
		if (itemStack.getCount() > 48) {
			i = 5;
		} else if (itemStack.getCount() > 32) {
			i = 4;
		} else if (itemStack.getCount() > 16) {
			i = 3;
		} else if (itemStack.getCount() > 1) {
			i = 2;
		}
		return i;
	}

	@Override
	public void render(VacItemProjectile itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
		float t;
		float s;
		poseStack.pushPose();
		ItemStack itemStack = itemEntity.getItem();
		int j = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
		this.random.setSeed(j);
		BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level(), null, itemEntity.getId());
		boolean bl = bakedModel.isGui3d();
		int k = this.getRenderAmount(itemStack);
		float l = Mth.sin(((float) itemEntity.getAge() + g) / 10.0f) * 0.1f + 0.1f;
		float m = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
		poseStack.translate(0.0f, l + 0.25f * m, 0.0f);
		float n = itemEntity.getSpin(g);
		poseStack.mulPose(Axis.YP.rotation(n));
		float o = bakedModel.getTransforms().ground.scale.x();
		float p = bakedModel.getTransforms().ground.scale.y();
		float q = bakedModel.getTransforms().ground.scale.z();
		if (!bl) {
			float r = -0.0f * (float) (k - 1) * 0.5f * o;
			s = -0.0f * (float) (k - 1) * 0.5f * p;
			t = -0.09375f * (float) (k - 1) * 0.5f * q;
			poseStack.translate(r, s, t);
		}
		for (int u = 0; u < k; ++u) {
			poseStack.pushPose();
			if (u > 0) {
				if (bl) {
					s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
					t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
					float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
					poseStack.translate(s, t, v);
				} else {
					s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
					t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
					poseStack.translate(s, t, 0.0f);
				}
			}
			this.itemRenderer.render(
					itemStack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY, bakedModel);
			poseStack.popPose();
			if (bl) {
				continue;
			}
			poseStack.translate(0.0f * o, 0.0f * p, 0.09375f * q);
		}
		poseStack.popPose();
		super.render(itemEntity, f, g, poseStack, multiBufferSource, i);
	}

	@Override
	public ResourceLocation getTextureLocation(VacItemProjectile entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
