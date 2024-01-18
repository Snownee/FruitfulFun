package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.bee.FFPlayer;
import snownee.fruits.bee.genetics.Allele;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket("set_gene_name")
public class CSetGeneNamePacket extends PacketHandler {
	public static CSetGeneNamePacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer serverPlayer) {
		char c = buf.readChar();
		FFPlayer.GeneName name = new FFPlayer.GeneName(buf.readUtf(), buf.readUtf());
		return executor.apply(() -> {
			if (Allele.byCode(c) == null) {
				return;
			}
			String code = String.valueOf(c);
			FFPlayer player = Objects.requireNonNull(FFPlayer.of(serverPlayer));
			String oldName = player.fruits$getGeneName(code);
			String oldDesc = player.fruits$getGeneDesc(code);
			if (oldName.equals(name.name()) && oldDesc.equals(name.desc())) {
				return;
			}
			player.fruits$setGeneName(code, name);
			SSyncPlayerPacket.send(serverPlayer);
		});
	}

	public static void send(String codename, String name, String desc) {
		I.sendToServer($ -> {
			$.writeChar(codename.charAt(0));
			$.writeUtf(name);
			$.writeUtf(desc);
		});
	}
}
