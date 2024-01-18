package snownee.fruits.bee.network;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.bee.FFPlayer;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "sync_player", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSyncPlayerPacket extends PacketHandler {
	public static SSyncPlayerPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer serverPlayer) {
		int size = buf.readVarInt();
		char[] codes = new char[size];
		FFPlayer.GeneName[] names = new FFPlayer.GeneName[size];
		for (int i = 0; i < size; i++) {
			codes[i] = buf.readChar();
			names[i] = new FFPlayer.GeneName(buf.readUtf(), buf.readUtf());
		}
		return executor.apply(() -> {
			FFPlayer player = FFPlayer.of(Minecraft.getInstance().player);
			if (player == null) {
				return;
			}
			for (int i = 0; i < size; i++) {
				player.fruits$setGeneName(String.valueOf(codes[i]), names[i]);
			}
		});
	}

	public static void send(ServerPlayer player) {
		Map<String, FFPlayer.GeneName> map = FFPlayer.of(player).fruits$getGeneNames();
		if (map.isEmpty()) {
			return;
		}
		I.send(player, $ -> {
			$.writeVarInt(map.size());
			map.forEach((code, name) -> {
				$.writeChar(code.charAt(0));
				$.writeUtf(name.name());
				$.writeUtf(name.desc());
			});
		});
	}
}
