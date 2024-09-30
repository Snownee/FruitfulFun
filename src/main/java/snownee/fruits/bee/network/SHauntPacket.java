package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.FruitfulFun;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KPacketTarget;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "haunt", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SHauntPacket extends PacketHandler {
	public static SHauntPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer serverPlayer) {
		int playerId = buf.readVarInt();
		int targetId = buf.readVarInt();
		return executor.apply(() -> {
			ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
			Entity player = level.getEntity(playerId);
			Entity target = level.getEntity(targetId);
			if (player == null || target == null) {
				return;
			}
			((FFPlayer) player).fruits$setHauntingTarget(target);
		});
	}

	public static void send(ServerPlayer player) {
		Consumer<FriendlyByteBuf> consumer = putData(player);
		I.send(player, consumer);
		I.send(KPacketTarget.tracking(player), consumer);
	}

	public static void send(ServerPlayer player, ServerPlayer seenBy) {
		I.send(seenBy, putData(player));
	}

	private static Consumer<FriendlyByteBuf> putData(ServerPlayer player) {
		return buf -> {
			buf.writeVarInt(player.getId());
			Entity target = ((FFPlayer) player).fruits$hauntingTarget();
			if (target == null) {
				target = player;
			}
			buf.writeVarInt(target.getId());
		};
	}
}
