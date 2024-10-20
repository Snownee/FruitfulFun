package snownee.fruits.mixin.food;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.ritual.RitualModule;

@Mixin(Interaction.class)
public class InteractionEntityMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		Interaction self = (Interaction) (Object) this;
		if (Hooks.food && RitualModule.isFFInteractionEntity(self)) {
			RitualModule.tickInteraction(self);
		}
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
		Interaction self = (Interaction) (Object) this;
		if (Hooks.food && RitualModule.isFFInteractionEntity(self)) {
			RitualModule.rightClickInteraction(self, player, interactionHand);
			cir.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
