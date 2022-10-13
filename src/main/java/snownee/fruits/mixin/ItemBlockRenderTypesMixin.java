package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {

	@Shadow(remap = false)
	private static ChunkRenderTypeSet CUTOUT_MIPPED;

	@Inject(at = @At(value = "RETURN", ordinal = 0), method = "getRenderLayers", cancellable = true, remap = false)
	private static void fruits_getRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> info) {
		Block block = state.getBlock();
		if (block instanceof FruitLeavesBlock) {
			info.setReturnValue(CUTOUT_MIPPED);
		}
	}

	@Inject(at = @At(value = "RETURN", ordinal = 0), method = "getChunkRenderType", cancellable = true)
	private static void fruits_getChunkRenderType(BlockState state, CallbackInfoReturnable<RenderType> info) {
		Block block = state.getBlock();
		if (block instanceof FruitLeavesBlock) {
			info.setReturnValue(RenderType.cutoutMipped());
		}
	}

}
