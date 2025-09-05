package snownee.fruits.mixin.crafter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
	@WrapOperation(
			method = "playerWillDestroy",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/BlockItem;setBlockEntityData(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/nbt/CompoundTag;)V"))
	private void setBlockEntityData(
			ItemStack stack,
			BlockEntityType<?> type,
			CompoundTag blockEntityData,
			Operation<Void> original,
			@Local BeehiveBlockEntity blockEntity) {
		if (Hooks.gadget && GadgetModule.BUZZY_CRAFTER_ENTITY.is(blockEntity.getType())) {
			type = GadgetModule.BUZZY_CRAFTER_ENTITY.get();
		}
		original.call(stack, type, blockEntityData);
	}
}
