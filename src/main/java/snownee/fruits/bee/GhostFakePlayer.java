package snownee.fruits.bee;

import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import snownee.fruits.util.FFFakePlayer;

public class GhostFakePlayer extends FFFakePlayer implements OwnableEntity {
	private @Nullable EntityReference<LivingEntity> owner;

	protected GhostFakePlayer(ServerLevel world, GameProfile profile) {
		super(world, profile);
	}

	public static GhostFakePlayer getOrCreate(ServerPlayer player) {
		String name = "FruitfulFunGhost " + player.getGameProfile().name();
		GhostFakePlayer fakePlayer = new GhostFakePlayer(player.level(), new GameProfile(UUID.randomUUID(), name));
		fakePlayer.owner = EntityReference.of(player);
		fakePlayer.setPos(player.position());
		fakePlayer.setRot(player.getYRot(), player.getXRot());
		player.level().addFreshEntity(fakePlayer);
		return fakePlayer;
	}

	@Override
	public @Nullable EntityReference<LivingEntity> getOwnerReference() {
		return owner;
	}
}
