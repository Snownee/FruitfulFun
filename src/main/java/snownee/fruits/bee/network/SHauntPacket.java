package snownee.fruits.bee.network;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SHauntPacket(int playerId, int targetId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SHauntPacket> TYPE = new CustomPacketPayload.Type<>(
			snownee.fruits.FruitfulFun.id("haunt"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SHauntPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			SHauntPacket::playerId,
			ByteBufCodecs.VAR_INT,
			SHauntPacket::targetId,
			SHauntPacket::new);

	@Override
	public CustomPacketPayload.Type<SHauntPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SHauntPacket> {
		@Override
		public void handle(SHauntPacket packet, PayloadContext context) {
			context.execute(() -> {
				ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
				Entity player = level.getEntity(packet.playerId());
				Entity target = level.getEntity(packet.targetId());
				if (player == null || target == null) {
					return;
				}
				FFPlayer.of(player).fruits$setHauntingTarget(target);
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SHauntPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(ServerPlayer player) {
		SHauntPacket packet = create(player);
		KPacketSender.send(packet, player);
		KPacketSender.sendToTracking(packet, player);
	}

	public static void send(ServerPlayer player, ServerPlayer seenBy) {
		KPacketSender.send(create(player), seenBy);
	}

	private static SHauntPacket create(ServerPlayer player) {
		Entity target = FFPlayer.of(player).fruits$hauntingTarget();
		if (target == null) {
			target = player;
		}
		return new SHauntPacket(player.getId(), target.getId());
	}
}
