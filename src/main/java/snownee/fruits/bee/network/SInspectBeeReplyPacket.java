package snownee.fruits.bee.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SInspectBeeReplyPacket(int id, List<Trait> traits, List<String> pollens, List<GeneRecord> genes)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SInspectBeeReplyPacket> TYPE = new CustomPacketPayload.Type<>(
			FruitfulFun.id("inspect_bee_reply"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SInspectBeeReplyPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			SInspectBeeReplyPacket::id,
			Trait.STREAM_CODEC.apply(ByteBufCodecs.list()),
			SInspectBeeReplyPacket::traits,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
			SInspectBeeReplyPacket::pollens,
			GeneRecord.STREAM_CODEC.apply(ByteBufCodecs.list()),
			SInspectBeeReplyPacket::genes,
			SInspectBeeReplyPacket::new);

	@Override
	public CustomPacketPayload.Type<SInspectBeeReplyPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SInspectBeeReplyPacket> {
		@Override
		public void handle(SInspectBeeReplyPacket packet, PayloadContext context) {
			context.execute(() -> {
				Minecraft mc = Minecraft.getInstance();
				if (mc.player == null) {
					return;
				}
				List<Trait> realTraits = packet.traits().stream().sorted().toList();
				InspectorClientHandler.writeToBook(packet.id(), mc.player, realTraits, packet.pollens(), packet.genes());
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SInspectBeeReplyPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(int id, ServerPlayer player, BeeAttributes attributes) {
		attributes.addInspected(player.getUUID());
		List<GeneRecord> genes = new ArrayList<>();
		for (Allele allele : Allele.sortedByCode()) {
			Locus locus = attributes.getLocus(allele);
			genes.add(new GeneRecord(allele.codename, locus.high(), locus.low()));
		}
		KPacketSender.send(
				new SInspectBeeReplyPacket(
						id,
						List.copyOf(attributes.genes().traits()),
						attributes.pollens(),
						List.copyOf(genes)),
				player);
	}

	public record GeneRecord(String code, int high, int low) {
		public static final StreamCodec<ByteBuf, GeneRecord> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				GeneRecord::code,
				ByteBufCodecs.VAR_INT,
				GeneRecord::high,
				ByteBufCodecs.VAR_INT,
				GeneRecord::low,
				GeneRecord::new);
	}
}
