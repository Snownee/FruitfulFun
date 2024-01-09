package snownee.fruits.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Final
	@Shadow
	private static Map<Block, RenderType> TYPE_BY_BLOCK;

	// Here we don't return a constant RenderType because some mods may change it

	@Inject(at = @At(value = "RETURN", ordinal = 0), method = "getChunkRenderType", cancellable = true)
	private static void fruits_getChunkRenderType(BlockState state, CallbackInfoReturnable<RenderType> ci) {
		if (state.getBlock() instanceof FruitLeavesBlock) {
			RenderType renderType = TYPE_BY_BLOCK.get(state.getBlock());
			ci.setReturnValue(renderType != null ? renderType : RenderType.solid());
		}
	}

	@Inject(at = @At(value = "RETURN", ordinal = 0), method = "getMovingBlockRenderType", cancellable = true)
	private static void fruits_getMovingBlockRenderType(BlockState state, CallbackInfoReturnable<RenderType> ci) {
		if (state.getBlock() instanceof FruitLeavesBlock) {
			RenderType renderType = TYPE_BY_BLOCK.get(state.getBlock());
			if (renderType != null) {
				ci.setReturnValue(renderType == RenderType.translucent() ? RenderType.translucentMovingBlock() : renderType);
			} else {
				ci.setReturnValue(RenderType.solid());
			}
		}
	}

}
