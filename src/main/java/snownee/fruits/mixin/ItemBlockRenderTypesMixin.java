package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {

	@Inject(at = @At(value = "RETURN", ordinal = 0), method = "getChunkRenderType", cancellable = true)
	private static void fruits_getChunkRenderType(BlockState state, CallbackInfoReturnable<RenderType> info) {
		Block block = state.getBlock();
		if (block instanceof FruitLeavesBlock) {
			info.setReturnValue(RenderType.cutoutMipped());
		}
	}

}
