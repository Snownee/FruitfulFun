package snownee.fruits.gadget.client;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.gadget.BuzzyCrafterBlockEntity;
import snownee.fruits.util.ClientProxy;

public final class BuzzyCrafterRenderer implements BlockEntityRenderer<BuzzyCrafterBlockEntity, BuzzyCrafterRenderState> {
	private final ItemModelResolver itemRenderer;
	private final EntityRenderDispatcher entityRenderer;

	public BuzzyCrafterRenderer(BlockEntityRendererProvider.Context context) {
		itemRenderer = context.itemModelResolver();
		entityRenderer = context.entityRenderer();
	}

	@Override
	public BuzzyCrafterRenderState createRenderState() {
		return new BuzzyCrafterRenderState();
	}

	@Override
	public void extractRenderState(
			BuzzyCrafterBlockEntity blockEntity,
			BuzzyCrafterRenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
//		state.item.extractItemGroupRenderState();
		state.item.entityType = EntityType.ITEM;
		ItemStack item = blockEntity.getTheItem();
		if (item.isEmpty()) {
			return;
		}
		state.hasBlockAbove = blockEntity.hasBlockAbove();
		state.item.ageInTicks = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() + partialTicks : 0;
		state.item.count = ItemClusterRenderState.getRenderedAmount(item.getCount());
		state.item.seed = ItemClusterRenderState.getSeedForItemStack(item);
		state.item.setData(ClientProxy.NO_BOB, Unit.INSTANCE);
		itemRenderer.updateForTopItem(state.item.item, item, ItemDisplayContext.GROUND, null, null, state.item.seed);
	}

	@Override
	public void submit(
			BuzzyCrafterRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {
		if (state.item.item.isEmpty()) {
			return;
		}
		entityRenderer.submit(state.item, camera, 0.5, state.hasBlockAbove ? 1.75 : 1.25, 0.5, poseStack, submitNodeCollector);
	}
}