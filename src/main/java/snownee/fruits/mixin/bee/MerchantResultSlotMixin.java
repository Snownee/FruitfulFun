package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {
	@Shadow
	@Final
	private Player player;

	@Inject(method = "checkTakeAchievements", at = @At("HEAD"))
	private void checkTakeAchievements(ItemStack carried, CallbackInfo ci) {
		if (carried.getCount() >= 50 && carried.getTag() != null && carried.getTag().getBoolean("FFTradeAdvancement")) {
			carried.getTag().remove("FFTradeAdvancement");
			if (carried.getTag().isEmpty()) {
				carried.setTag(null);
			}
			Hooks.awardSimpleAdvancement(player, "apiarist");
		}
	}
}
