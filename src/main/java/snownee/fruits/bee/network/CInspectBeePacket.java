package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "inspect_bee", dir = KiwiPacket.Direction.PLAY_TO_SERVER)
public class CInspectBeePacket extends PacketHandler {
	public static CInspectBeePacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		Objects.requireNonNull(player);
		InspectTarget target = InspectTarget.fromNetwork(buf);
		if (target == null) {
			return null;
		}
		return executor.apply(() -> {
			Entity entity = target.getEntity(player.level());
			FFPlayer.of(player).fruits$maybeInitGenes();
			if (entity instanceof Bee bee) {
				Advancement advancement = Hooks.advancement((ServerLevel) player.level(), "inspector");
				if (advancement != null) {
					player.getAdvancements().award(advancement, "_");
				}
				SInspectBeeReplyPacket.send(player, BeeAttributes.of(bee));
			}
		});
	}
}
