package snownee.fruits.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import snownee.fruits.Hooks;
import snownee.fruits.vacuum.VacGunItem;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"), cancellable = true)
	private void handleVacGun(CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.vac && player != null && player.getMainHandItem().getItem() instanceof VacGunItem) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void handleVacGun(boolean bl, CallbackInfo ci) {
		if (bl && Hooks.vac && player != null && player.getMainHandItem().getItem() instanceof VacGunItem) {
			VacGunItem.shoot(player, InteractionHand.MAIN_HAND);
			ci.cancel();
		}
	}
}
