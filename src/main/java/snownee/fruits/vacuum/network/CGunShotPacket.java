package snownee.fruits.vacuum.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import snownee.fruits.vacuum.VacGunItem;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket("gun_shot")
public class CGunShotPacket extends PacketHandler {
	public static CGunShotPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer player) {
		return executor.apply(() -> {
			Objects.requireNonNull(player);
			if (!player.isRemoved() && !player.isSpectator() && player.getMainHandItem().getItem() instanceof VacGunItem) {
				VacGunItem.shoot(player, InteractionHand.MAIN_HAND);
			}
		});
	}
}
