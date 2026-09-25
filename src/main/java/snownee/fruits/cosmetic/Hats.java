package snownee.fruits.cosmetic;

import org.jspecify.annotations.Nullable;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import snownee.fruits.Hooks;
import snownee.fruits.compat.trinkets.TrinketsCompat;
import snownee.fruits.cosmetic.item.FlowerCrownItem;

/**
 * Head-item (hat / flower crown) helpers. Everything here is only meaningful while
 * {@link CosmeticModule} is loaded.
 */
public final class Hats {
	private Hats() {
	}

	@Nullable
	public static FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.getItem() instanceof FlowerCrownItem item) {
			return item;
		}
		if (Hooks.trinkets) {
			return TrinketsCompat.getFlowerCrown(entity);
		}
		return null;
	}

	public static boolean isWearingFlowerCrown(LivingEntity entity) {
		return getFlowerCrown(entity) != null;
	}

	/**
	 * Handles head-cosmetic swapping when a player interacts with an entity. Works
	 * for any living entity that has the {@code head/cosmetic} trinket slot; the
	 * default pack only grants it to bees. The interaction is driven purely by the
	 * held item's {@code fruitfulfun:head_cosmetic} tag and the entity's slot.
	 * Returns {@link InteractionResult#PASS} when this does not apply.
	 */
	public static InteractionResult interact(Player player, InteractionHand hand, LivingEntity entity) {
		if (!Hooks.trinkets || !TrinketsCompat.hasHeadSlot(entity)) {
			return InteractionResult.PASS;
		}
		if (!player.hasInfiniteMaterials() && !Trusts.canEdit(player, entity)) {
			return InteractionResult.PASS;
		}
		ItemStack held = player.getItemInHand(hand);
		// Sneak with empty hands to just take the worn item back; otherwise swap
		// it with whatever head cosmetic is held.
		if (player.isSecondaryUseActive()) {
			if (player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) {
				swapHeadItem(player, hand, entity, ItemStack.EMPTY);
				return InteractionResult.SUCCESS_SERVER;
			}
		} else if (held.is(CosmeticModule.HEAD_COSMETIC)) {
			swapHeadItem(player, hand, entity, held);
			return InteractionResult.SUCCESS_SERVER;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Mirrors {@link net.minecraft.world.entity.decoration.ArmorStand#swapItem} for the head
	 * trinket slot. {@code handStack} is what the player is offering (empty means "just take it").
	 */
	private static void swapHeadItem(Player player, InteractionHand hand, LivingEntity entity, ItemStack handStack) {
		if (player.level().isClientSide()) {
			return;
		}
		ItemStack worn = TrinketsCompat.takeHeadItem(entity);
		// ArmorStand#swapItem: in creative with an empty slot, place a copy without
		// consuming the held stack, so no phantom item appears in the inventory.
		if (player.hasInfiniteMaterials() && worn.isEmpty() && !handStack.isEmpty()) {
			TrinketsCompat.equipHeadItem(entity, handStack.copyWithCount(1));
			playEquipSound(entity, handStack);
			entity.gameEvent(GameEvent.EQUIP, player);
			return;
		}
		if (handStack.isEmpty() || handStack.getCount() <= 1) {
			TrinketsCompat.equipHeadItem(entity, handStack);
			player.setItemInHand(hand, worn);
			if (!worn.isEmpty()) {
				playEquipSound(entity, worn);
				entity.gameEvent(GameEvent.UNEQUIP, player);
			}
			if (!handStack.isEmpty()) {
				playEquipSound(entity, handStack);
				entity.gameEvent(GameEvent.EQUIP, player);
			}
			return;
		}
		if (!worn.isEmpty()) {
			return;
		}
		TrinketsCompat.equipHeadItem(entity, handStack.split(1));
		playEquipSound(entity, handStack);
		entity.gameEvent(GameEvent.EQUIP, player);
	}

	private static void playEquipSound(LivingEntity entity, ItemStack stack) {
		entity.level().playSound(null, entity, TrinketsCompat.getEquipSound(entity, stack).value(), SoundSource.PLAYERS, 1.0f, 1.0f);
	}
}
