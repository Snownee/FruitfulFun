package snownee.fruits.cherry.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import snownee.fruits.cherry.block.SlidingDoorTileEntity;

public class SlidingDoorTileEntityRenderer extends TileEntityRenderer<SlidingDoorTileEntity> {

	public SlidingDoorTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(SlidingDoorTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		// TODO Auto-generated method stub

	}

}
