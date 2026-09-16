package snownee.fruits.minigame.network;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snownee.fruits.FruitfulFun;
import snownee.fruits.minigame.MinigameManager;
import snownee.fruits.minigame.PathRules;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CUpdatePathPacket(List<Integer> path) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CUpdatePathPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("minigame_update_path"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CUpdatePathPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(PathRules.MAX_PATH)),
			CUpdatePathPacket::path,
			CUpdatePathPacket::new);

	@Override
	public CustomPacketPayload.Type<CUpdatePathPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CUpdatePathPacket> {
		@Override
		public void handle(CUpdatePathPacket packet, PayloadContext context) {
			MinigameManager.updatePath(context.serverPlayer(), packet.path());
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CUpdatePathPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(List<Integer> path) {
		KPacketSender.sendToServer(new CUpdatePathPacket(path));
	}
}