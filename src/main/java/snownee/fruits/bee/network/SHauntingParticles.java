package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.bee.BeeModule;
import snownee.kiwi.network.KPacketTarget;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "haunting_particles", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SHauntingParticles extends PacketHandler {
	public static SHauntingParticles I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		Vec3 vec3 = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
		return executor.apply(() -> {
			ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
			int count = 1 + level.random.nextInt(3);
			for (int i = 0; i < count; i++) {
				double x = vec3.x + (level.random.nextDouble() - 0.5D) * 0.5D;
				double y = vec3.y + (level.random.nextDouble() - 0.5D) * 0.5D;
				double z = vec3.z + (level.random.nextDouble() - 0.5D) * 0.5D;
				level.addAlwaysVisibleParticle(BeeModule.GHOST.get(), x, y, z, 0.0D, 0.0D, 0.0D);
			}
		});
	}

	public static void send(ServerLevel level, Vec3 vec3) {
		I.send(KPacketTarget.tracking(level, BlockPos.containing(vec3)), buf -> {
			buf.writeFloat((float) vec3.x);
			buf.writeFloat((float) vec3.y);
			buf.writeFloat((float) vec3.z);
		});
	}

//	public static void spawnOnEntity(ServerPlayer player, Entity target) {
//		AABB box = target.getBoundingBox();
//		Vec3 eyePosition = player.getEyePosition();
//		Vec3 center = box.getCenter();
//		Vec3 vec3 = box.clip(eyePosition, center).orElse(center);
//		send(player.serverLevel(), vec3);
//	}
}
