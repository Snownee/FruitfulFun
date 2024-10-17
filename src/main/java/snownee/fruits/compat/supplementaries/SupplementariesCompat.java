package snownee.fruits.compat.supplementaries;

import net.mehvahdjukaar.moonlight.api.block.IColored;
import net.mehvahdjukaar.supplementaries.common.misc.mob_container.IMobContainerProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.fruits.vacuum.client.ItemProjectileColor;

public class SupplementariesCompat {
	public static Entity getTargetEntity(BlockEntity be) {
		if (be instanceof IMobContainerProvider provider) {
			return provider.getMobContainer().getDisplayedMob();
		}
		return null;
	}

	public static ItemProjectileColor getItemProjectileColor(ItemStack itemStack) {
		if (itemStack.getItem() instanceof IColored colored) {
			return ItemProjectileColor.ofDyeColor(colored.getColor());
		}
		return null;
	}
}
