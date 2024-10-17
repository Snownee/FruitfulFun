package snownee.fruits.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import snownee.fruits.bee.GhostFakePlayer;

@Mixin(targets = "net.minecraftforge.common.util.FakePlayer$FakePlayerNetHandler")
public abstract class FakePlayerNetHandlerMixin extends ServerGamePacketListenerImpl {
	public FakePlayerNetHandlerMixin(
			MinecraftServer p_9770_,
			Connection p_9771_,
			ServerPlayer p_9772_) {
		super(p_9770_, p_9771_, p_9772_);
	}

	@Inject(method = "handleUseItemOn", at = @At("HEAD"), cancellable = true)
	private void handleUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
		if (this.player instanceof GhostFakePlayer) {
			super.handleUseItemOn(packet);
			ci.cancel();
		}
	}

	@Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
	private void handleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
		if (this.player instanceof GhostFakePlayer) {
			super.handleInteract(packet);
			ci.cancel();
		}
	}
}
