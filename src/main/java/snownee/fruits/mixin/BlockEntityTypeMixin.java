package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

	@Inject(at = @At("HEAD"), method = "isValid", cancellable = true)
	public void fruits_isValid(BlockState pState, CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this == BlockEntityType.SIGN && pState.getBlock() instanceof SignBlock) {
			cir.setReturnValue(true);
		}
	}

}
