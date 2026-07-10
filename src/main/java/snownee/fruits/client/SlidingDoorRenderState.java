package snownee.fruits.client;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SlidingDoorRenderState extends EntityRenderState {
	public final BlockModelRenderState topModel = new BlockModelRenderState();
	public final BlockModelRenderState bottomModel = new BlockModelRenderState();
	public int lightCoordsAbove = 15728880;
}
