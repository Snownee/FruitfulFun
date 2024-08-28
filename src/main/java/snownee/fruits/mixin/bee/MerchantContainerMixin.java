package snownee.fruits.mixin.bee;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.GeneData;

@Mixin(MerchantContainer.class)
public class MerchantContainerMixin {
	@WrapOperation(
			method = "updateSellItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/trading/MerchantOffer;assemble()Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack updateSellItem(MerchantOffer merchantOffer, Operation<ItemStack> original, @Local(ordinal = 0) ItemStack offer) {
		ItemStack output = original.call(merchantOffer);
		if (!BeeModule.isBeehiveTrade(merchantOffer)) {
			return output;
		}
		output.removeTagKey("FFTrade");
		CompoundTag blockEntityData = BlockItem.getBlockEntityData(offer);
		if (blockEntityData == null) {
			return ItemStack.EMPTY;
		}
		ListTag list = Objects.requireNonNull(blockEntityData).getList(BeehiveBlockEntity.BEES, Tag.TAG_COMPOUND);
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int value = BeeModule.getBeesValue(list.stream().map(tag -> {
			CompoundTag lociTag = ((CompoundTag) tag)
					.getCompound(BeehiveBlockEntity.ENTITY_DATA)
					.getCompound("FruitfulFun")
					.getCompound("Genes");
			GeneData geneData = new GeneData();
			geneData.fromNBT(lociTag);
			geneData.updateTraits();
			return geneData;
		}).toList());
		output.setCount(Math.min(output.getMaxStackSize(), value));
		if (output.getCount() >= 50) {
			output.getOrCreateTag().putBoolean("FFTradeAdvancement", true);
		}
		return output;
	}
}
