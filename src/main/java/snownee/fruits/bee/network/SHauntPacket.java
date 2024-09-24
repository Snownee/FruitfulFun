package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "haunt", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SHauntPacket extends PacketHandler {
	public static SHauntPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		int id = buf.readVarInt();
		return executor.apply(() -> {
			LocalPlayer localPlayer = Objects.requireNonNull(Minecraft.getInstance().player);
			Entity target = localPlayer.level().getEntity(id);
			if (target != null) {
				((FFPlayer) localPlayer).fruits$setHauntingTarget(target);
			}
		});
	}

	public static void send(ServerPlayer player, Entity target) {
		I.send(player, buf -> buf.writeVarInt(target.getId()));
	}
}
