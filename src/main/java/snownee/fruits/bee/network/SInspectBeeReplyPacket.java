package snownee.fruits.bee.network;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "inspect_bee_reply", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SInspectBeeReplyPacket extends PacketHandler {
	public static SInspectBeeReplyPacket I;

	public static void send(ServerPlayer player, BeeAttributes attributes) {
		SInspectBeeReplyPacket.I.send(player, buf0 -> {
			buf0.writeCollection(attributes.getTraits().stream().map(Trait::name).toList(), FriendlyByteBuf::writeUtf);
			buf0.writeCollection(attributes.getPollens(), FriendlyByteBuf::writeUtf);
			Map<Allele, Locus> loci = attributes.getLoci();
			buf0.writeVarInt(Allele.sortedByCode().size());
			for (Allele allele : Allele.sortedByCode()) {
				Locus locus = loci.get(allele);
				buf0.writeChar(allele.codename);
				buf0.writeVarInt(locus.getHigh());
				buf0.writeVarInt(locus.getLow());
			}
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		List<String> traits = buf.readList(FriendlyByteBuf::readUtf);
		List<String> pollens = buf.readList(FriendlyByteBuf::readUtf);
		List<GeneRecord> genes = buf.readList($ -> new GeneRecord($.readChar(), $.readVarInt() + 1, $.readVarInt() + 1));
		return executor.apply(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) {
				return;
			}
			List<Trait> realTraits = traits.stream()
					.sorted()
					.map(Trait.REGISTRY::get)
					.filter(Objects::nonNull)
					.toList();
			InspectorClientHandler.writeToBook(mc.player, realTraits, pollens, genes);
		});
	}

	public record GeneRecord(char code, int high, int low) {
	}
}
