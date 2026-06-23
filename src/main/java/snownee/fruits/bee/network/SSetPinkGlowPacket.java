package snownee.fruits.bee.network;

import java.util.Objects;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.FruitfulFun;
import snownee.fruits.duck.FFLivingEntity;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SSetPinkGlowPacket(IntList affectedEntities) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SSetPinkGlowPacket> TYPE = new CustomPacketPayload.Type<>(
			FruitfulFun.id("set_pink_glow"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SSetPinkGlowPacket> STREAM_CODEC = StreamCodec.composite(
			StreamCodec.of(RegistryFriendlyByteBuf::writeIntIdList, RegistryFriendlyByteBuf::readIntIdList),
			SSetPinkGlowPacket::affectedEntities,
			SSetPinkGlowPacket::new);

	@Override
	public CustomPacketPayload.Type<SSetPinkGlowPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SSetPinkGlowPacket> {
		@Override
		public void handle(SSetPinkGlowPacket packet, PayloadContext context) {
			context.execute(() -> {
				ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
				packet.affectedEntities().intStream().mapToObj(level::getEntity).filter(Objects::nonNull).forEach(entity -> {
					if (entity instanceof FFLivingEntity living) {
						living.fruits$setPinkGlowing();
					}
				});
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SSetPinkGlowPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(ServerPlayer player, IntList affectedEntities) {
		KPacketSender.send(new SSetPinkGlowPacket(affectedEntities), player);
	}
}
