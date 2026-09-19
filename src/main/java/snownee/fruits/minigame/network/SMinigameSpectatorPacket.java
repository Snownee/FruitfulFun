package snownee.fruits.minigame.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.FruitBoardScreen;
import snownee.fruits.minigame.MinigameSession;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SMinigameSpectatorPacket(String name, boolean joined) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SMinigameSpectatorPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("minigame_spectator"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SMinigameSpectatorPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.stringUtf8(32),
			SMinigameSpectatorPacket::name,
			ByteBufCodecs.BOOL,
			SMinigameSpectatorPacket::joined,
			SMinigameSpectatorPacket::new);

	@Override
	public CustomPacketPayload.Type<SMinigameSpectatorPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SMinigameSpectatorPacket> {
		@Override
		public void handle(SMinigameSpectatorPacket packet, PayloadContext context) {
			context.execute(() -> FruitBoardScreen.showSpectatorToast(packet));
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SMinigameSpectatorPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(MinigameSession session, String name, boolean joined) {
		SMinigameSpectatorPacket packet = new SMinigameSpectatorPacket(name, joined);
		KPacketSender.send(packet, session.player());
		MinigameSession opponent = session.opponent();
		if (opponent != null) {
			KPacketSender.send(packet, opponent.player());
		}
	}
}
