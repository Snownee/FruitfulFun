package snownee.fruits.vacuum;

import java.util.List;
import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.util.FFFakePlayer;

public class ContainerOpenerFakePlayer extends FFFakePlayer {
	private long openContainerSince;

	public static ContainerOpenerFakePlayer getOrCreate(ServerLevel level, BlockPos pos) {
		String name = "FruitfulFunOpener " + pos.toShortString();
		List<ContainerOpenerFakePlayer> entities = level.getEntitiesOfClass(
				ContainerOpenerFakePlayer.class, new AABB(pos), $ -> $.getName().getString().equals(name));
		if (!entities.isEmpty()) {
			return entities.get(0);
		}
		ContainerOpenerFakePlayer player = new ContainerOpenerFakePlayer(level, new GameProfile(null, name));
		player.setPos(Vec3.atCenterOf(pos));
		level.addFreshEntity(player);
		return player;
	}

	public ContainerOpenerFakePlayer(ServerLevel level, GameProfile profile) {
		super(level, profile);
	}

	@Override
	public OptionalInt openMenu(@Nullable MenuProvider menuProvider) {
		if (menuProvider == null) {
			return OptionalInt.empty();
		}
		AbstractContainerMenu containerMenu = menuProvider.createMenu(containerCounter, getInventory(), this);
		if (containerMenu == null) {
			return OptionalInt.empty();
		}
		this.containerMenu = containerMenu;
		openContainerSince = level().getGameTime();
		return OptionalInt.of(0);
	}

	@Override
	public void closeContainer() {
		doCloseContainer();
	}

	@Override
	public void tick() {
		super.tick();
		if (level().getGameTime() - openContainerSince > 12) {
			closeContainer();
			discard();
		}
	}
}
