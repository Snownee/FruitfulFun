package snownee.fruits.compat.trinkets;

import org.jspecify.annotations.Nullable;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.item.FlowerCrownItem;

public class TrinketsCompat {
	private static final TrinketCallback FLOWER_CROWN = new TrinketCallback() {
		@Override
		public void tick(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
			if (entity.level().isClientSide()) {
				FlowerCrownItem.spawnParticles(entity);
			}
		}
	};

	public static void init() {
		TrinketCallback.setCallback(CherryModule.CHERRY_CROWN.get(), FLOWER_CROWN);
		TrinketCallback.setCallback(CherryModule.REDLOVE_CROWN.get(), FLOWER_CROWN);
	}

	public static @Nullable FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		return TrinketsApi.getAttachment(entity)
				.findFirst(itemStack -> itemStack.getItem() instanceof FlowerCrownItem)
				.map(slot -> (FlowerCrownItem) slot.get().getItem())
				.orElse(null);
	}

	public static TrinketSlotAccess headSlot(LivingEntity entity) {
		return TrinketsApi.getAttachment(entity).getSlotAccess("head/hat", 0);
	}

	public static boolean hasHeadItem(LivingEntity entity) {
		return !headSlot(entity).get().isEmpty();
	}

	public static void equipHeadItem(LivingEntity entity, ItemStack stack) {
		headSlot(entity).set(stack);
	}

	public static ItemStack takeHeadItem(LivingEntity entity) {
		TrinketSlotAccess slot = headSlot(entity);
		ItemStack stack = slot.get();
		slot.set(ItemStack.EMPTY);
		return stack;
	}

	public static Holder<SoundEvent> getEquipSound(LivingEntity entity, ItemStack stack) {
		TrinketSlotAccess slot = headSlot(entity);
		return TrinketCallback.getCallback(stack).getEquipSound(stack, slot, entity);
	}
}
