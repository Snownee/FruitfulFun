package snownee.fruits.bee;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.OwnableEntity;
import snownee.fruits.util.FFFakePlayer;

public class GhostFakePlayer extends FFFakePlayer implements OwnableEntity {
	private UUID ownerUUID;

	protected GhostFakePlayer(ServerLevel world, GameProfile profile) {
		super(world, profile);
	}

	public static GhostFakePlayer getOrCreate(ServerPlayer player) {
		String name = "FruitfulFunGhost " + player.getGameProfile().getName();
		GhostFakePlayer fakePlayer = new GhostFakePlayer(player.serverLevel(), new GameProfile(null, name));
		fakePlayer.ownerUUID = player.getUUID();
		fakePlayer.setPos(player.position());
		fakePlayer.setRot(player.getYRot(), player.getXRot());
		player.level().addFreshEntity(fakePlayer);
		return fakePlayer;
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return ownerUUID;
	}
}
