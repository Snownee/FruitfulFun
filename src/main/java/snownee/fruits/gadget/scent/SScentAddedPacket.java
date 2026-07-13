package snownee.fruits.gadget.scent;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SScentAddedPacket(BlockPos pos, int color) implements CustomPacketPayload {
	public static final Type<SScentAddedPacket> TYPE = new Type<>(FruitfulFun.id("scent_added"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SScentAddedPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			SScentAddedPacket::pos,
			ByteBufCodecs.INT,
			SScentAddedPacket::color,
			SScentAddedPacket::new);

	@Override
	public Type<SScentAddedPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SScentAddedPacket> {
		@Override
		public void handle(SScentAddedPacket packet, PayloadContext context) {
			context.execute(() -> {
				Vec3 particlePos = Vec3.atBottomCenterOf(packet.pos());
				ParticleType<ColorParticleOption> particleType = ParticleTypes.ENTITY_EFFECT;
				ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
				RandomSource random = level.getRandom();

				float red = (packet.color() >> 16 & 0xFF) / 255.0F;
				float green = (packet.color() >> 8 & 0xFF) / 255.0F;
				float blue = (packet.color() & 0xFF) / 255.0F;
				for (int i = 0; i < 50; i++) {
					double dist = random.nextDouble() * 0.5F;
					double angle = random.nextDouble() * Math.PI * 2.0;
					double velocityX = Math.cos(angle) * dist;
					double velocityY = 0.01 + random.nextDouble() * 0.5;
					double velocityZ = Math.sin(angle) * dist;
					float randomBrightness = 0.75F + random.nextFloat() * 0.25F;
					ColorParticleOption particle = ColorParticleOption.create(
							particleType,
							ARGB.colorFromFloat(0.5F, red * randomBrightness, green * randomBrightness, blue * randomBrightness)
					);
					level.addParticle(
							particle,
							particlePos.x + velocityX * 0.1,
							particlePos.y + 0.3,
							particlePos.z + velocityZ * 0.1,
							velocityX,
							velocityY,
							velocityZ);
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SScentAddedPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(LivingEntity entity, int color) {
		KPacketSender.sendToTracking(new SScentAddedPacket(entity.blockPosition(), color), entity);
	}
}
