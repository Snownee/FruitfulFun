package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

	@Inject(at = @At("HEAD"), method = "isValid", cancellable = true)
	private void fruits_isValid(BlockState pState, CallbackInfoReturnable<Boolean> cir) {
		BlockEntityType<?> type = (BlockEntityType<?>) (Object) this;
		Class<?> blockClass = pState.getBlock().getClass();
		if (type == BlockEntityType.SIGN && (blockClass == WallSignBlock.class || blockClass == StandingSignBlock.class)) {
			cir.setReturnValue(true);
			return;
		}
		if (type == BlockEntityType.HANGING_SIGN && (blockClass == WallHangingSignBlock.class || blockClass == CeilingHangingSignBlock.class)) {
			cir.setReturnValue(true);
			return;
		}
	}

}
