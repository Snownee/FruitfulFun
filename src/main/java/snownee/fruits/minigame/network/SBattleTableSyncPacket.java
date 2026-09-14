package snownee.fruits.minigame.network;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.BattleTableBlockEntity;
import snownee.fruits.minigame.BattleTableScreen;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SBattleTableSyncPacket(
		BlockPos pos,
		String left,
		String right,
		List<String> spectators,
		boolean leftAvailable,
		boolean rightAvailable,
		boolean canStart,
		boolean canSpectate,
		boolean waitingForLeft,
		boolean close) implements CustomPacketPayload {
	public static final Type<SBattleTableSyncPacket> TYPE = new Type<>(FruitfulFun.id("battle_table_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SBattleTableSyncPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			SBattleTableSyncPacket::pos,
			ByteBufCodecs.STRING_UTF8,
			SBattleTableSyncPacket::left,
			ByteBufCodecs.STRING_UTF8,
			SBattleTableSyncPacket::right,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(256)),
			SBattleTableSyncPacket::spectators,
			ByteBufCodecs.BOOL,
			SBattleTableSyncPacket::leftAvailable,
			ByteBufCodecs.BOOL,
			SBattleTableSyncPacket::rightAvailable,
			ByteBufCodecs.BOOL,
			SBattleTableSyncPacket::canStart,
			ByteBufCodecs.BOOL,
			SBattleTableSyncPacket::canSpectate,
			ByteBufCodecs.BOOL,
			SBattleTableSyncPacket::waitingForLeft,
			ByteBufCodecs.BOOL,
			SBattleTableSyncPacket::close,
			SBattleTableSyncPacket::new);

	@Override
	public Type<SBattleTableSyncPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SBattleTableSyncPacket> {
		@Override
		public void handle(SBattleTableSyncPacket packet, PayloadContext context) {
			context.execute(() -> BattleTableScreen.open(packet));
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SBattleTableSyncPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(BattleTableBlockEntity table, ServerPlayer player) {
		UUID leftId = table.left();
		UUID rightId = table.right();
		ServerPlayer left = leftId == null ? null : player.level().getServer().getPlayerList().getPlayer(leftId);
		ServerPlayer right = rightId == null ? null : player.level().getServer().getPlayerList().getPlayer(rightId);
		List<String> spectators = table.spectators().stream()
				.map(id -> player.level().getServer().getPlayerList().getPlayer(id))
				.filter(Objects::nonNull)
				.map(ServerPlayer::getScoreboardName)
				.toList();
		boolean isLeft = left == player;
		boolean isRight = right == player;
		KPacketSender.send(new SBattleTableSyncPacket(
				table.getBlockPos(),
				left == null ? "" : left.getScoreboardName(),
				right == null ? "" : right.getScoreboardName(),
				spectators,
				 table.left() == null,
				table.right() == null,
				isLeft && right != null,
				isLeft || isRight,
				isRight && table.left() != null,
				false),
			player);
	}

	public static void close(BattleTableBlockEntity table, ServerPlayer player) {
		KPacketSender.send(new SBattleTableSyncPacket(table.getBlockPos(), "", "", List.of(), false, false, false, false, false, true), player);
	}
}
