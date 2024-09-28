package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket("set_gene_name")
public class CSetGeneNamePacket extends PacketHandler {
	public static CSetGeneNamePacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		char c = buf.readChar();
		FFPlayer.GeneName name = new FFPlayer.GeneName(buf.readUtf(), buf.readUtf());
		return executor.apply(() -> {
			if (Allele.byCode(c) == null) {
				return;
			}
			String code = String.valueOf(c);
			FFPlayer ffPlayer = Objects.requireNonNull(FFPlayer.of(player));
			String oldName = ffPlayer.fruits$getGeneName(code);
			String oldDesc = ffPlayer.fruits$getGeneDesc(code);
			if (oldName.equals(name.name()) && oldDesc.equals(name.desc())) {
				return;
			}
			ffPlayer.fruits$setGeneName(code, name);
			SSyncPlayerPacket.send(player);
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
