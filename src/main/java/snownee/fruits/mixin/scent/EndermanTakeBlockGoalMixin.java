package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(EnderMan.EndermanLeaveBlockGoal.class)
public class EndermanTakeBlockGoalMixin {
	@Inject(method = "canPlaceBlock", at = @At("HEAD"), cancellable = true)
	private void canUse(
			Level level,
			BlockPos pos,
			BlockState carried,
			BlockState targetState,
			BlockState belowState,
			BlockPos below,
			CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.gadget && GadgetModule.ENDER.get().isActiveAt(level.getChunkAt(pos))) {
			cir.setReturnValue(false);
		}
	}
}
