package snownee.fruits.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import snownee.fruits.Hooks;

@Mixin(Connection.class)
public class ServerGamePacketListenerImplMixin {
	@Inject(
			method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
			at = @At("HEAD"),
			cancellable = true)
	private void fruits$cancelPacketSending(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
		if (packet instanceof ClientboundUpdateMobEffectPacket p && Hooks.scentEffects.contains(p.getEffect().value())) {
			ci.cancel();
		}
	}
}
