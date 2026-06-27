package snownee.fruits.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.world.entity.EntityType;
import snownee.fruits.block.entity.SlidingDoorEntity;

public class SlidingDoorRenderer extends EntityRenderer<SlidingDoorEntity, BlockDisplayEntityRenderState> {

	public SlidingDoorRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	public BlockDisplayEntityRenderState createRenderState() {
		return new BlockDisplayEntityRenderState();
	}

	@Override
	public void extractRenderState(SlidingDoorEntity entity, BlockDisplayEntityRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.entityType = EntityType.BLOCK_DISPLAY;
	}
}
