package snownee.fruits.mixin.bee;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.trading.MerchantOffer;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.GeneData;

@Mixin(MerchantContainer.class)
public class MerchantContainerMixin {
	@WrapOperation(
			method = "updateSellItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/trading/MerchantOffer;assemble()Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack updateSellItem(MerchantOffer offer, Operation<ItemStack> original, @Local(name = "buyA") ItemStack buyA) {
		ItemStack output = original.call(offer);
		if (!BeeModule.isBeehiveTrade(offer)) {
			return output;
		}
		Bees bees = buyA.getOrDefault(DataComponents.BEES, Bees.EMPTY);
		if (bees.bees().isEmpty()) {
			return ItemStack.EMPTY;
		}
		output.remove(BeeModule.MERCHANT_OFFER.get());
		int value = BeeModule.getBeesValue(bees.bees().stream().map(occupant -> {
			try {
				CompoundTag data = occupant.entityData().copyTagWithoutId()
						.getCompoundOrEmpty("FruitfulFun");
				if (!data.contains("Genes")) {
					return null;
				}
				return data.read("Genes", GeneData.CODEC).orElse(null);
			} catch (Exception _) {
				return null;
			}
		}).filter(Objects::nonNull).toList());
		output.setCount(Math.min(output.getMaxStackSize(), value));
		if (output.getCount() >= 50) {
			output.set(BeeModule.MERCHANT_OFFER_ADVANCEMENT.get(), "apiarist");
		}
		return output;
	}
}
