package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(value = Player.class, priority = 3000)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "blockActionRestricted", at = @At("HEAD"), cancellable = true)
	private void blockActionRestricted(Level level, BlockPos pos, GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && ((FFPlayer) this).fruits$isHaunting()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
	private void aiStep(CallbackInfo ci) {
		if (Hooks.bee && ((FFPlayer) this).fruits$isHaunting()) {
			super.aiStep();
			ci.cancel();
		}
	}
}
