package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.duck.FFLivingEntity;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "set_pink_glow", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSetPinkGlowPacket extends PacketHandler {
	public static SSetPinkGlowPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		IntList affectedEntities = buf.readIntIdList();
		return executor.apply(() -> {
			ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
			affectedEntities.intStream().mapToObj(level::getEntity).filter(Objects::nonNull).forEach(entity -> {
				if (entity instanceof FFLivingEntity living) {
					living.fruits$setPinkGlowing();
				}
			});
		});
	}

	public static void send(ServerPlayer player, IntList affectedEntities) {
		I.send(player, buf -> buf.writeIntIdList(affectedEntities));
	}
}
