package snownee.fruits.compat.supplementaries;

import net.mehvahdjukaar.supplementaries.common.misc.mob_container.IMobContainerProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SupplementariesCompat {
	public static Entity getTargetEntity(BlockEntity be) {
		if (be instanceof IMobContainerProvider provider) {
			return provider.getMobContainer().getDisplayedMob();
		}
		return null;
	}
}
