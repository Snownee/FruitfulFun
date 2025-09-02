package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.FFRegistries;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.gadget.ScentType;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "inspect_target", dir = KiwiPacket.Direction.PLAY_TO_SERVER)
public class CInspectTargetPacket extends PacketHandler {
	public static CInspectTargetPacket I;

	@Override
	public @Nullable CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		Objects.requireNonNull(player);
		InspectTarget target = InspectTarget.fromNetwork(buf);
		if (target == null) {
			return null;
		}
		return executor.apply(() -> {
			Level level = player.level();
			if (!target.isFor(level)) {
				return;
			}
			InspectAction action = InspectAction.get(target, level);
			if (action == InspectAction.BEE) {
				Entity entity = target.getEntity(level);
				FFPlayer.of(player).fruits$maybeInitGenes();
				if (entity instanceof Bee bee) {
					Hooks.awardSimpleAdvancement(player, "inspector");
					SInspectBeeReplyPacket.send(player, BeeAttributes.of(bee));
				}
			} else if (action == InspectAction.SCENT) {
				LevelChunk chunk = level.getChunkAt(((InspectTarget.BlockTarget) target).pos());
				long gameTime = level.getGameTime();
				int count = 0;
				long firstEnds = Long.MAX_VALUE;
				for (ScentType type : FFRegistries.SCENT_TYPE) {
					long time = type.getTime(chunk);
					if (time > gameTime) {
						count++;
						if (time < firstEnds) {
							firstEnds = time;
						}
					}
				}
				Component component;
				if (count == 0) {
					component = Component.translatable("tip.fruitfulfun.analyzeScents.none");
				} else {
					long s = (firstEnds - gameTime) / 20;
					if (count == 1) {
						component = Component.translatable("tip.fruitfulfun.analyzeScents.singular", s);
					} else {
						component = Component.translatable("tip.fruitfulfun.analyzeScents.plural", count, s);
					}
				}
				player.displayClientMessage(component, true);
			}
		});
	}
}
