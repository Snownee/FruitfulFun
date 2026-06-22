package snownee.fruits.bee.network;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeModule;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SHauntingParticlesPacket(Vec3 pos) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SHauntingParticlesPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id(
			"haunting_particles"));

	@Override
	public CustomPacketPayload.Type<SHauntingParticlesPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SHauntingParticlesPacket> {
		public static final StreamCodec<RegistryFriendlyByteBuf, SHauntingParticlesPacket> STREAM_CODEC = StreamCodec.composite(
				Vec3.STREAM_CODEC,
				SHauntingParticlesPacket::pos,
				SHauntingParticlesPacket::new);

		@Override
		public void handle(SHauntingParticlesPacket packet, PayloadContext context) {
			if (!FFCommonConfig.hauntingInteractionParticles) {
				return;
			}
			context.execute(() -> {
				Vec3 vec3 = packet.pos();
				ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
				int count = 1 + level.getRandom().nextInt(3);
				for (int i = 0; i < count; i++) {
					double x = vec3.x + (level.getRandom().nextDouble() - 0.5D) * 0.5D;
					double y = vec3.y + (level.getRandom().nextDouble() - 0.5D) * 0.5D;
					double z = vec3.z + (level.getRandom().nextDouble() - 0.5D) * 0.5D;
					level.addAlwaysVisibleParticle(BeeModule.GHOST.get(), x, y, z, 0.0D, 0.0D, 0.0D);
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SHauntingParticlesPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(ServerLevel level, Vec3 pos) {
		KPacketSender.sendToTracking(new SHauntingParticlesPacket(pos), level, BlockPos.containing(pos));
	}
}
