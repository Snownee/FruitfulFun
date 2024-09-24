package snownee.fruits.bee;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.util.FFFakePlayer;

public class GhostFakePlayer extends FFFakePlayer {
	protected GhostFakePlayer(ServerLevel world, GameProfile profile) {
		super(world, profile);
	}

	public static GhostFakePlayer getOrCreate(ServerPlayer player) {
		String name = "FruitfulFunGhost " + player.getGameProfile().getName();
		GhostFakePlayer fakePlayer = new GhostFakePlayer(player.serverLevel(), new GameProfile(null, name));
		fakePlayer.setPos(player.position());
		fakePlayer.setRot(player.getYRot(), player.getXRot());
		player.level().addFreshEntity(fakePlayer);
		return fakePlayer;
	}
}
