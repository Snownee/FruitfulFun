package snownee.fruits.bee.network;

import java.util.Objects;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.FFRegistries;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.gadget.ScentType;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CInspectTargetPacket(InspectTarget target) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CInspectTargetPacket> TYPE = new CustomPacketPayload.Type<>(
			snownee.fruits.FruitfulFun.id("inspect_target"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CInspectTargetPacket> STREAM_CODEC = StreamCodec.of(
			(buf, packet) -> packet.target().toNetwork(buf),
			buf -> new CInspectTargetPacket(Objects.requireNonNull(InspectTarget.fromNetwork(buf))));

	@Override
	public CustomPacketPayload.Type<CInspectTargetPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CInspectTargetPacket> {
		@Override
		public void handle(CInspectTargetPacket packet, PayloadContext context) {
			context.execute(() -> {
				ServerPlayer player = context.serverPlayer();
				Level level = player.level();
				InspectTarget target = packet.target();
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
					player.sendOverlayMessage(component);
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CInspectTargetPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(InspectTarget target) {
		KPacketSender.sendToServer(new CInspectTargetPacket(target));
	}
}
