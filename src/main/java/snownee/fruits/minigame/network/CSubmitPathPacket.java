package snownee.fruits.minigame.network;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.MinigameManager;
import snownee.fruits.minigame.PathRules;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CSubmitPathPacket(List<Integer> path) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CSubmitPathPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("minigame_submit_path"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CSubmitPathPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(PathRules.MAX_PATH)),
			CSubmitPathPacket::path,
			CSubmitPathPacket::new);

	@Override
	public CustomPacketPayload.Type<CSubmitPathPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CSubmitPathPacket> {
		@Override
		public void handle(CSubmitPathPacket packet, PayloadContext context) {
			ServerPlayer player = context.serverPlayer();
			MinigameManager.submitPath(player, packet.path());
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CSubmitPathPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(List<Integer> path) {
		KPacketSender.sendToServer(new CSubmitPathPacket(path));
	}
}