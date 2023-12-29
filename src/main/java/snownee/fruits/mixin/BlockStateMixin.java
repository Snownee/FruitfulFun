package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(BlockStateBase.class)
public abstract class BlockStateMixin {

	@Inject(at = @At("HEAD"), method = "hasBlockEntity", cancellable = true)
	private void fruits_hasBlockEntity(CallbackInfoReturnable<Boolean> ci) {
		if (getBlock() instanceof FruitLeavesBlock leavesBlock) {
			ci.setReturnValue(leavesBlock.hasBlockEntity((BlockState) (Object) this));
		}
	}

	@Shadow
	public abstract Block getBlock();

}
