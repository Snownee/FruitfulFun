package snownee.fruits.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import snownee.fruits.block.entity.SlidingDoorEntity;

public class SlidingDoorRenderer extends EntityRenderer<SlidingDoorEntity, BlockDisplayEntityRenderState> {

	public SlidingDoorRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	public BlockDisplayEntityRenderState createRenderState() {
		return null;
	}
}
