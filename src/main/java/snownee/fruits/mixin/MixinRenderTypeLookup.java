package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(RenderTypeLookup.class)
public class MixinRenderTypeLookup {

    @Inject(at = @At("HEAD"), method = "canRenderInLayer", cancellable = true, remap = false)
    private static void fruits_canRenderInLayer(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> info) {
        Block block = state.getBlock();
        if (block instanceof FruitLeavesBlock) {
            info.setReturnValue(type == RenderType.getCutoutMipped());
        }
    }

}
