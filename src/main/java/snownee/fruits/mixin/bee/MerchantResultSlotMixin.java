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
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {
	@Shadow
	@Final
	private Player player;

	@Inject(method = "checkTakeAchievements", at = @At("HEAD"))
	private void checkTakeAchievements(ItemStack stack, CallbackInfo ci) {
		FruitfulFun.LOGGER.info("Stack: {}, Tag: {}", stack, stack.getTag());
		if (stack.getCount() >= 50 && stack.getTag() != null && stack.getTag().getBoolean("FFTradeAdvancement")) {
			stack.getTag().remove("FFTradeAdvancement");
			if (stack.getTag().isEmpty()) {
				stack.setTag(null);
			}
			Hooks.awardSimpleAdvancement(player, "apiarist");
		}
	}
}
