package snownee.fruits.minigame.network;

import java.util.List;
import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.MinigameSession;
import snownee.fruits.util.ClientProxy;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SMinigameSyncPacket(
		Optional<BoardState> board,
		Optional<BoardState> opponentBoard,
		PlayerSync self,
		PlayerSync opponent,
		int timeRemaining,
		boolean finished,
		String playerName,
		String opponentName,
		int result,
		boolean spectating,
		boolean open,
		float cascadeSpeed) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SMinigameSyncPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("minigame_sync"));

	private static final StreamCodec<RegistryFriendlyByteBuf, Optional<BoardState>> BOARD = ByteBufCodecs.optional(
			BoardState.STREAM_CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, SMinigameSyncPacket> STREAM_CODEC = StreamCodec.composite(
			BOARD,
			SMinigameSyncPacket::board,
			BOARD,
			SMinigameSyncPacket::opponentBoard,
			PlayerSync.STREAM_CODEC,
			SMinigameSyncPacket::self,
			PlayerSync.STREAM_CODEC,
			SMinigameSyncPacket::opponent,
			ByteBufCodecs.VAR_INT,
			SMinigameSyncPacket::timeRemaining,
			ByteBufCodecs.BOOL,
			SMinigameSyncPacket::finished,
			ByteBufCodecs.stringUtf8(32),
			SMinigameSyncPacket::playerName,
			ByteBufCodecs.stringUtf8(32),
			SMinigameSyncPacket::opponentName,
			ByteBufCodecs.VAR_INT,
			SMinigameSyncPacket::result,
			ByteBufCodecs.BOOL,
			SMinigameSyncPacket::spectating,
			ByteBufCodecs.BOOL,
			SMinigameSyncPacket::open,
			ByteBufCodecs.FLOAT,
			SMinigameSyncPacket::cascadeSpeed,
			SMinigameSyncPacket::new);

	@Override
	public CustomPacketPayload.Type<SMinigameSyncPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SMinigameSyncPacket> {
		@Override
		public void handle(SMinigameSyncPacket packet, PayloadContext context) {
			context.execute(() -> ClientProxy.openMinigameScreen(packet));
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SMinigameSyncPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(MinigameSession session, boolean open) {
		MinigameSession opponent = session.opponent();
		Optional<BoardState> board = open ? Optional.of(BoardState.of(session.board())) : Optional.empty();
		Optional<BoardState> opponentBoard = open
				? Optional.of(opponent == null ? BoardState.EMPTY : BoardState.of(opponent.board()))
				: Optional.empty();
		PlayerSync self = PlayerSync.of(session, open);
		PlayerSync other = opponent == null ? PlayerSync.EMPTY : PlayerSync.of(opponent, open);
		send(session, board, opponentBoard, self, other, open, session.player());
		for (ServerPlayer spectator : List.copyOf(session.spectators())) {
			send(session, board, opponentBoard, self, other, open, spectator);
		}
	}

	public static void send(MinigameSession session, boolean open, ServerPlayer receiver) {
		MinigameSession opponent = session.opponent();
		send(
				session,
				open ? Optional.of(BoardState.of(session.board())) : Optional.empty(),
				open
						? Optional.of(opponent == null ? BoardState.EMPTY : BoardState.of(opponent.board()))
						: Optional.empty(),
				PlayerSync.of(session, open),
				opponent == null ? PlayerSync.EMPTY : PlayerSync.of(opponent, open),
				open,
				receiver);
	}

	private static void send(
			MinigameSession session,
			Optional<BoardState> board,
			Optional<BoardState> opponentBoard,
			PlayerSync self,
			PlayerSync opponent,
			boolean open,
			ServerPlayer receiver) {
		KPacketSender.send(
				new SMinigameSyncPacket(
						board,
						opponentBoard,
						self,
						opponent,
						session.timeRemaining(),
						session.isFinished(),
						session.playerName(),
						session.opponentName(),
						session.result(),
						receiver != session.player(),
						open,
						session.cascadeSpeed()),
				receiver);
	}
}