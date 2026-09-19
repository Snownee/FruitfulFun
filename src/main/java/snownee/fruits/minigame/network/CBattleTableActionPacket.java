package snownee.fruits.minigame.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.MinigameManager;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CBattleTableActionPacket(BlockPos pos, int action) implements CustomPacketPayload {
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int LEAVE = 2;
	public static final int START = 3;
	public static final int SPECTATE = 4;
	public static final Type<CBattleTableActionPacket> TYPE = new Type<>(FruitfulFun.id("battle_table_action"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CBattleTableActionPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			CBattleTableActionPacket::pos,
			ByteBufCodecs.VAR_INT,
			CBattleTableActionPacket::action,
			CBattleTableActionPacket::new);

	@Override
	public Type<CBattleTableActionPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CBattleTableActionPacket> {
		@Override
		public void handle(CBattleTableActionPacket packet, PayloadContext context) {
			context.execute(() -> MinigameManager.tableAction(context.serverPlayer(), packet.pos(), packet.action()));
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CBattleTableActionPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(BlockPos pos, int action) {
		KPacketSender.sendToServer(new CBattleTableActionPacket(pos, action));
	}
}
