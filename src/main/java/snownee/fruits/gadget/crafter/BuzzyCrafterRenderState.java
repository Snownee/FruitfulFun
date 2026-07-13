package snownee.fruits.gadget.crafter;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;

public class BuzzyCrafterRenderState extends BlockEntityRenderState {
	ItemEntityRenderState item = new ItemEntityRenderState();
	boolean hasBlockAbove;
}
