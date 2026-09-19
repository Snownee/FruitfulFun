package snownee.fruits.minigame.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.MinigameManager;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CStartMinigamePacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CStartMinigamePacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("minigame_start"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CStartMinigamePacket> STREAM_CODEC = StreamCodec.unit(
			new CStartMinigamePacket());

	@Override
	public CustomPacketPayload.Type<CStartMinigamePacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CStartMinigamePacket> {
		@Override
		public void handle(CStartMinigamePacket packet, PayloadContext context) {
			MinigameManager.startSession(context.serverPlayer());
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CStartMinigamePacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send() {
		KPacketSender.sendToServer(new CStartMinigamePacket());
	}
}
