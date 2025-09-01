package snownee.fruits.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import snownee.fruits.Hooks;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Inject(
			method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
			at = @At("HEAD"),
			cancellable = true)
	private void fruits$cancelPacketSending(Packet<?> packet, @Nullable PacketSendListener listener, CallbackInfo ci) {
		if (packet instanceof ClientboundUpdateMobEffectPacket p && Hooks.hiddenEffects.contains(p.getEffect())) {
			ci.cancel();
		}
	}
}
