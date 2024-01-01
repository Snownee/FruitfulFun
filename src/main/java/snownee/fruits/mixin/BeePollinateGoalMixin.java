package snownee.fruits.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;

@Mixin(Bee.BeePollinateGoal.class)
public abstract class BeePollinateGoalMixin {

	@Shadow(aliases = {"field_20377", "b"}, remap = false)
	private Bee this$0;

	@Final
	@Mutable
	@Shadow
	private final Predicate<BlockState> VALID_POLLINATION_BLOCKS = Hooks::canPollinate;

	@Inject(method = "stop", at = @At("HEAD"))
	private void fruits_stop(CallbackInfo cir) {
		if (!Hooks.bee || this$0.getSavedFlowerPos() == null) {
			return;
		}
		Hooks.onPollinateComplete(this$0);
	}

	@ModifyExpressionValue(method = "canBeeUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private boolean fruits_canBeeUse(boolean original) {
		return original && !BeeAttributes.of(this$0).hasTrait(Trait.RAIN_CAPABLE);
	}

	@ModifyExpressionValue(method = "canBeeContinueToUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private boolean fruits_canBeeContinueToUse(boolean original) {
		return original && !BeeAttributes.of(this$0).hasTrait(Trait.RAIN_CAPABLE);
	}

}
