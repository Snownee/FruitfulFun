package snownee.fruits.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;

@Mixin(Bee.BeePollinateGoal.class)
public abstract class BeePollinateGoalMixin {

	@Shadow(aliases = { "f_28062_", "b" }, remap = false)
	private Bee this$0;

	@Shadow
	private final Predicate<BlockState> VALID_POLLINATION_BLOCKS = Hooks::canPollinate;

	@Inject(method = "stop", at = @At("HEAD"))
	public void onComplete(CallbackInfo cir) {
		if (!Hooks.hybridization || this$0.getSavedFlowerPos() == null) {
			return;
		}
		Hooks.onPollinateComplete(this$0);
	}
}
