package snownee.fruits.gadget.vac;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.gadget.GadgetModule;

public class VacGunManager {
	private final Level level;
	private final Set<Player> sources = Sets.newLinkedHashSet();

	public VacGunManager(Level level) {
		this.level = level;
	}

	public void addSource(Player player) {
		sources.add(player);
	}

	public void removeSource(Player player) {
		sources.remove(player);
	}

	public void tick() {
		for (Player player : sources.stream()
				.filter(player -> player.isRemoved() || !GadgetModule.VAC_GUN.is(player.getUseItem()))
				.toList()) {
			removeSource(player);
		}
		sources.forEach(this::tickSource);
	}

	private void tickSource(Player player) {
		Vec3 pos = player.getEyePosition().lerp(player.position(), 0.5F);
		for (Entity entity : level.getPushableEntities(player, AABB.ofSize(pos, 8, 8, 8))) {
			if (!canAffect(entity)) {
				return;
			}
		}
	}

	public boolean canAffect(Entity entity) {
		if (entity.is(GadgetModule.VAC_IMMOVABLE)) {
			return false;
		}
		if (entity.is(GadgetModule.VAC_MOVABLE)) {
			return true;
		}
		return true;
	}
}
