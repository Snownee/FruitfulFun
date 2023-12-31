package snownee.fruits.cherry.item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.kiwi.item.ModItem;

public class WreathItem extends ModItem implements Equipable {
	public WreathItem(Properties builder) {
		super(builder.stacksTo(1));
	}

	@Override
	public @NotNull EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public @NotNull SoundEvent getEquipSound() {
		//TODO
		return Equipable.super.getEquipSound();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		return swapWithEquipmentSlot(this, level, player, interactionHand);
	}
}
