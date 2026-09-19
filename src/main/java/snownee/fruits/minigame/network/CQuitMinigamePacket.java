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
public record CQuitMinigamePacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CQuitMinigamePacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("minigame_quit"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CQuitMinigamePacket> STREAM_CODEC = StreamCodec.unit(new CQuitMinigamePacket());

	@Override
	public CustomPacketPayload.Type<CQuitMinigamePacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CQuitMinigamePacket> {
		@Override
		public void handle(CQuitMinigamePacket packet, PayloadContext context) {
			MinigameManager.quit(context.serverPlayer());
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CQuitMinigamePacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send() {
		KPacketSender.sendToServer(new CQuitMinigamePacket());
	}
}