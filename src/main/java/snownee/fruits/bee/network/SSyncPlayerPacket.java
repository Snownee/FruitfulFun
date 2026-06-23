package snownee.fruits.bee.network;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SSyncPlayerPacket(List<GeneNameRecord> names) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SSyncPlayerPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("sync_player"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SSyncPlayerPacket> STREAM_CODEC = StreamCodec.composite(
			GeneNameRecord.STREAM_CODEC.apply(ByteBufCodecs.list()), SSyncPlayerPacket::names, SSyncPlayerPacket::new);

	@Override
	public CustomPacketPayload.Type<SSyncPlayerPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SSyncPlayerPacket> {
		@Override
		public void handle(SSyncPlayerPacket packet, PayloadContext context) {
			context.execute(() -> {
				FFPlayer player = FFPlayer.of(Minecraft.getInstance().player);
				if (player == null) {
					return;
				}
				for (GeneNameRecord name : packet.names()) {
					player.fruits$setGeneName(name.code(), new FFPlayer.GeneName(name.name(), name.desc()));
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SSyncPlayerPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(ServerPlayer player) {
		Map<String, FFPlayer.GeneName> map = FFPlayer.of(player).fruits$getGeneNames();
		if (map.isEmpty()) {
			return;
		}
		List<GeneNameRecord> names = map.entrySet().stream().map(entry -> new GeneNameRecord(
				entry.getKey(),
				entry.getValue().name(),
				entry.getValue().desc())).toList();
		KPacketSender.send(new SSyncPlayerPacket(names), player);
	}

	public record GeneNameRecord(String code, String name, String desc) {
		public GeneNameRecord {
			Objects.requireNonNull(name);
			Objects.requireNonNull(desc);
		}

		public static final StreamCodec<ByteBuf, GeneNameRecord> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				GeneNameRecord::code,
				ByteBufCodecs.STRING_UTF8,
				GeneNameRecord::name,
				ByteBufCodecs.STRING_UTF8,
				GeneNameRecord::desc,
				GeneNameRecord::new);
	}
}
