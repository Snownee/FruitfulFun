package snownee.fruits.mixin.bee;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.ritual.BeehiveIngredient;
import snownee.fruits.util.CommonProxy;

@Mixin(MerchantOffer.class)
public class MerchantOfferMixin {
	@WrapOperation(
			method = "satisfiedBy",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/ItemCost;test(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean satisfiedBy(ItemCost itemCost, ItemStack itemStack, Operation<Boolean> original) {
		boolean result = original.call(itemCost, itemStack);
		MerchantOffer offer = (MerchantOffer) (Object) this;
		if (BeeModule.isBeehiveTrade(offer) && CommonProxy.isBeehive(itemStack)) {
			return result && BeehiveIngredient.TRUE.test(itemStack);
		}
		return result;
	}

	@WrapMethod(method = "getCostA")
	private ItemStack getCostA(Operation<ItemStack> original) {
		ItemStack itemStack = original.call();
		MerchantOffer offer = (MerchantOffer) (Object) this;
		if (BeeModule.isBeehiveTrade(offer)) {
			itemStack.set(DataComponents.ITEM_NAME, Component.translatable("tip.fruitfulfun.beehiveTradeInputName"));
			itemStack.set(BeeModule.MERCHANT_OFFER.get(), Unit.INSTANCE);
			itemStack.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("tip.fruitfulfun.beehiveTradeInputHint"))));
		}
		return itemStack;
	}
}
