package snownee.fruits.bee.network;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeVariant;
import snownee.fruits.bee.genetics.Trait;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SSyncBeePacket(
		int id,
		List<UUID> trusted,
		Holder<BeeVariant> variant,
		List<Trait> traits,
		long mutagenEndsIn) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SSyncBeePacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("sync_bee"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SSyncBeePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			SSyncBeePacket::id,
			UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
			SSyncBeePacket::trusted,
			BeeVariant.STREAM_CODEC,
			SSyncBeePacket::variant,
			Trait.STREAM_CODEC.apply(ByteBufCodecs.list()),
			SSyncBeePacket::traits,
			ByteBufCodecs.LONG,
			SSyncBeePacket::mutagenEndsIn,
			SSyncBeePacket::new);

	@Override
	public CustomPacketPayload.Type<SSyncBeePacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SSyncBeePacket> {
		@Override
		public void handle(SSyncBeePacket packet, PayloadContext context) {
			context.execute(() -> {
				Entity entity = Objects.requireNonNull(Minecraft.getInstance().level).getEntity(packet.id());
				if (entity instanceof Bee) {
					BeeAttributes attributes = BeeAttributes.of(entity);
					attributes.setTrusted(packet.trusted());
					attributes.setForcedVariant(packet.variant());
					attributes.genes().setTraits(packet.traits());
					attributes.setMutagenEndsIn(packet.mutagenEndsIn(), entity.level().getGameTime());
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SSyncBeePacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(Bee bee) {
		SSyncBeePacket packet = create(bee);
		KPacketSender.sendToTracking(packet, bee);
	}

	public static void send(Bee bee, ServerPlayer player) {
		KPacketSender.send(create(bee), player);
	}

	private static SSyncBeePacket create(Bee bee) {
		BeeAttributes attributes = BeeAttributes.of(bee);
		return new SSyncBeePacket(
				bee.getId(),
				attributes.getTrusted(),
				attributes.variant(),
				List.copyOf(attributes.genes().traits()),
				attributes.getMutagenEndsIn());
	}
}
