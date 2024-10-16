package snownee.fruits.mixin.bee;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HybridizingRecipeType;
import snownee.fruits.bee.genetics.Trait;

@Mixin(Bee.BeePollinateGoal.class)
public abstract class BeePollinateGoalMixin {

	@Shadow(aliases = {"field_20377", "f_28062_"}, remap = false)
	private Bee this$0;

	@Final
	@Mutable
	@Shadow
	private Predicate<BlockState> VALID_POLLINATION_BLOCKS;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(Bee bee, CallbackInfo ci) {
		if (Hooks.bee) {
			VALID_POLLINATION_BLOCKS = Hooks.wrapPollinationPredicate(VALID_POLLINATION_BLOCKS);
		}
	}

	@Inject(method = "stop", at = @At("HEAD"))
	private void stop(CallbackInfo cir) {
		if (!Hooks.bee || this$0.getSavedFlowerPos() == null) {
			return;
		}
		BeeModule.RECIPE_TYPE.get().onPollinateComplete(this$0);
		HybridizingRecipeType.removeOverflownPollens(this$0);
	}

	@ModifyExpressionValue(method = "canBeeUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private boolean canBeeUseRainCapable(boolean original) {
		return original && !BeeAttributes.of(this$0).hasTrait(Trait.RAIN_CAPABLE);
	}

	@Inject(method = "canBeeUse", at = @At("HEAD"), cancellable = true)
	private void canBeeUse(CallbackInfoReturnable<Boolean> cir) {
		if (BeeAttributes.of(this$0).hasTrait(Trait.GHOST)) {
			cir.setReturnValue(false);
		}
	}

	@ModifyExpressionValue(
			method = "canBeeContinueToUse",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private boolean canBeeContinueToUse(boolean original) {
		return original && !BeeAttributes.of(this$0).hasTrait(Trait.RAIN_CAPABLE);
	}

}
