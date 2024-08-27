package snownee.fruits.mixin.vac;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.vacuum.VacGunItem;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
	private void playerTouch(Player player, CallbackInfo ci) {
		if (!Hooks.vac) {
			return;
		}
		ItemEntity self = (ItemEntity) (Object) this;
		if (self.level().isClientSide) {
			return;
		}
		if (player.getUseItem().getItem() instanceof VacGunItem) {
			VacGunItem.collectItem(player, self, player.getUseItem(), null);
			ci.cancel();
		}
	}
}
