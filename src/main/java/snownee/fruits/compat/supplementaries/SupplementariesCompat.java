package snownee.fruits.compat.supplementaries;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.fruits.gadget.client.ItemProjectileColor;

public class SupplementariesCompat {
	public static @Nullable Entity getTargetEntity(BlockEntity be) {
//		if (be instanceof IMobContainerProvider provider) {
//			return provider.getMobContainer().getDisplayedMob();
//		}
		return null;
	}

	public static @Nullable ItemProjectileColor getItemProjectileColor(ItemStack itemStack) {
//		if (itemStack.getItem() instanceof IColored colored) {
//			return ItemProjectileColor.ofDyeColor(colored.getColor());
//		}
		return null;
	}
}
