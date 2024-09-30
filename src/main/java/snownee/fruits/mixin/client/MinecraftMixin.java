package snownee.fruits.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import snownee.fruits.bee.network.CHauntingActionPacket;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void startAttack(CallbackInfoReturnable<Boolean> cir) {
		if (CHauntingActionPacket.canDoAction(player)) {
			CHauntingActionPacket.I.sendToServer(buf -> {});
			cir.setReturnValue(true);
		}
	}
}
