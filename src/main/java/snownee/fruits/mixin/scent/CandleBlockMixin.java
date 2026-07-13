package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.scent.ScentedCandleBlock;

@Mixin(CandleBlock.class)
public abstract class CandleBlockMixin extends AbstractCandleBlock {
	protected CandleBlockMixin(Properties properties) {
		super(properties);
	}

	@Shadow
	@Override
	protected abstract boolean canBeLit(BlockState state);

	@Inject(method = "canLight", at = @At("HEAD"), cancellable = true)
	private static void canLight(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.gadget && state.getBlock() instanceof ScentedCandleBlock) {
			cir.setReturnValue(((CandleBlockMixin) state.getBlock()).canBeLit(state));
		}
	}
}
