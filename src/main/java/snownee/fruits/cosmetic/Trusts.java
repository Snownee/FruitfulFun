package snownee.fruits.cosmetic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;

public final class Trusts {
	private Trusts() {
	}

	public static boolean canEdit(Player player, LivingEntity entity) {
		if (Hooks.bee && entity instanceof Bee bee) {
			return BeeAttributes.of(bee).trusts(player.getUUID());
		}
		if (entity instanceof OwnableEntity ownable && ownable.getOwnerReference() != null && ownable.getOwnerReference().matches(player)) {
			return false;
		}
		return true;
	}
}
