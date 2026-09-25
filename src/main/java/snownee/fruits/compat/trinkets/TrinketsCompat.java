package snownee.fruits.compat.trinkets;

import org.jspecify.annotations.Nullable;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.cosmetic.CosmeticModule;
import snownee.fruits.cosmetic.item.FlowerCrownItem;

public class TrinketsCompat {
	public static final String HEAD_SLOT = "head/cosmetic";

	private static final TrinketCallback FLOWER_CROWN = new TrinketCallback() {
		@Override
		public void tick(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
			if (entity.level().isClientSide()) {
				FlowerCrownItem.spawnParticles(entity);
			}
		}
	};

	public static void init() {
		TrinketCallback.setCallback(CosmeticModule.CHERRY_CROWN.get(), FLOWER_CROWN);
		TrinketCallback.setCallback(CosmeticModule.REDLOVE_CROWN.get(), FLOWER_CROWN);
		TrinketsApi.registerTrinketPredicate(
				Identifier.parse("fruitfulfun:head_cosmetic"),
				(stack, slot, entity) -> stack.is(CosmeticModule.HEAD_COSMETIC));
	}

	public static @Nullable FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		return TrinketsApi.getAttachment(entity)
				.findFirst(itemStack -> itemStack.getItem() instanceof FlowerCrownItem)
				.map(slot -> (FlowerCrownItem) slot.get().getItem())
				.orElse(null);
	}

	public static @Nullable TrinketSlotAccess headSlot(LivingEntity entity) {
		return TrinketsApi.getAttachment(entity).getSlotAccess(HEAD_SLOT, 0);
	}

	public static boolean hasHeadSlot(LivingEntity entity) {
		return headSlot(entity) != null;
	}

	public static boolean hasHeadItem(LivingEntity entity) {
		TrinketSlotAccess slot = headSlot(entity);
		return slot != null && !slot.get().isEmpty();
	}

	public static void equipHeadItem(LivingEntity entity, ItemStack stack) {
		TrinketSlotAccess slot = headSlot(entity);
		if (slot != null) {
			slot.set(stack);
		}
	}

	public static ItemStack takeHeadItem(LivingEntity entity) {
		TrinketSlotAccess slot = headSlot(entity);
		if (slot == null) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.get();
		slot.set(ItemStack.EMPTY);
		return stack;
	}

	public static Holder<SoundEvent> getEquipSound(LivingEntity entity, ItemStack stack) {
		TrinketSlotAccess slot = headSlot(entity);
		return TrinketCallback.getCallback(stack).getEquipSound(stack, slot, entity);
	}
}
