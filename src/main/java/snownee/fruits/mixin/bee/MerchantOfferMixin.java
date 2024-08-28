package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.util.CommonProxy;

@Mixin(MerchantOffer.class)
public class MerchantOfferMixin {
	@Inject(
			method = "isRequiredItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"),
			cancellable = true)
	private void isRequiredItem(ItemStack offer, ItemStack cost, CallbackInfoReturnable<Boolean> cir) {
		if (!BeeModule.isBeehiveTrade((MerchantOffer) (Object) this)) {
			return;
		}
		if (!CommonProxy.isBeehive(offer)) {
			cir.setReturnValue(false);
			return;
		}
		CompoundTag blockEntityData = BlockItem.getBlockEntityData(offer);
		if (blockEntityData == null) {
			cir.setReturnValue(false);
			return;
		}
		ListTag list = blockEntityData.getList(BeehiveBlockEntity.BEES, Tag.TAG_COMPOUND);
		cir.setReturnValue(!list.isEmpty());
	}
}
