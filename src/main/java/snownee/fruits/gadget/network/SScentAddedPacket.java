package snownee.fruits.gadget.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.network.KPacketTarget;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "scent_added", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SScentAddedPacket extends PacketHandler {
	public static SScentAddedPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer serverPlayer) {
		BlockPos pos = buf.readBlockPos();
		int color = buf.readInt();
		return executor.apply(() -> {
			Vec3 particlePos = Vec3.atBottomCenterOf(pos);
			ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
			RandomSource random = level.getRandom();

			float red = (color >> 16 & 0xFF) / 255.0F;
			float green = (color >> 8 & 0xFF) / 255.0F;
			float blue = (color & 0xFF) / 255.0F;
			for (int i = 0; i < 50; i++) {
				double dist = random.nextDouble() * 0.5F;
				double angle = random.nextDouble() * Math.PI * 2.0;
				double velocityX = Math.cos(angle) * dist;
				double velocityY = 0.01 + random.nextDouble() * 0.5;
				double velocityZ = Math.sin(angle) * dist;
				float randomBrightness = 0.75F + random.nextFloat() * 0.25F;
				Particle particle = Minecraft.getInstance().particleEngine.createParticle(
						ParticleTypes.EFFECT,
						particlePos.x + velocityX * 0.1,
						particlePos.y + 0.3,
						particlePos.z + velocityZ * 0.1,
						velocityX,
						velocityY,
						velocityZ);
				if (particle != null) {
					particle.setColor(red * randomBrightness, green * randomBrightness, blue * randomBrightness);
					particle.scale((float) dist);
				}
			}
		});
	}

	public static void send(LivingEntity entity, int color) {
		Consumer<FriendlyByteBuf> consumer = buf -> {
			buf.writeBlockPos(entity.blockPosition());
			buf.writeInt(color);
		};
		I.send(KPacketTarget.tracking(entity), consumer);
	}
}
