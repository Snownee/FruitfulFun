package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;

@Mixin(Mob.class)
public class RideableBeeMobMixin {
	@Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
	private void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
		if (!Hooks.bee) {
			return;
		}
		Mob mob = (Mob) (Object) this;
		if (!mob.isNoAi() && mob instanceof Bee bee && bee.getFirstPassenger() instanceof Player player) {
			BeeAttributes attributes = BeeAttributes.of(bee);
			if (attributes.isSaddled() && player.isCreative() || attributes.trusts(player.getUUID())) {
				cir.setReturnValue(player);
			}
		}
	}

	@Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
	private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci) {
		if (!Hooks.bee) {
			return;
		}
		Mob mob = (Mob) (Object) this;
		if (mob instanceof Bee bee && !bee.isDeadOrDying()) {
			InteractionResult result = Hooks.playerInteractBee(player, hand, bee);
			if (result.consumesAction()) {
				ci.setReturnValue(result);
			}
		}
	}
}
