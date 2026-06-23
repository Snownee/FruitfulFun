package snownee.fruits.bee.network;

import java.util.Objects;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CSetGeneNamePacket(SSyncPlayerPacket.GeneNameRecord gene) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CSetGeneNamePacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("set_gene_name"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CSetGeneNamePacket> STREAM_CODEC = StreamCodec.composite(
			SSyncPlayerPacket.GeneNameRecord.STREAM_CODEC,
			CSetGeneNamePacket::gene,
			CSetGeneNamePacket::new);

	@Override
	public CustomPacketPayload.Type<CSetGeneNamePacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CSetGeneNamePacket> {
		@Override
		public void handle(CSetGeneNamePacket packet, PayloadContext context) {
			ServerPlayer player = context.serverPlayer();
			SSyncPlayerPacket.GeneNameRecord nameRecord = packet.gene();
			if (Allele.byCode(nameRecord.code()) == null) {
				return;
			}
			String code = nameRecord.code();
			FFPlayer ffPlayer = Objects.requireNonNull(FFPlayer.of(player));
			String oldName = ffPlayer.fruits$getGeneName(code);
			String oldDesc = ffPlayer.fruits$getGeneDesc(code);
			if (oldName.equals(nameRecord.name()) && oldDesc.equals(nameRecord.desc())) {
				return;
			}
			ffPlayer.fruits$setGeneName(code, new FFPlayer.GeneName(nameRecord.name(), nameRecord.desc()));
			SSyncPlayerPacket.send(player);
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CSetGeneNamePacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(String codename, String name, String desc) {
		KPacketSender.sendToServer(new CSetGeneNamePacket(new SSyncPlayerPacket.GeneNameRecord(codename, name, desc)));
	}
}
