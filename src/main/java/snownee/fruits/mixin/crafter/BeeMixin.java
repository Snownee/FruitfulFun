package snownee.fruits.mixin.crafter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(Bee.class)
public class BeeMixin {
	@WrapOperation(
			method = "isHiveValid", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getType()Lnet/minecraft/world/level/block/entity/BlockEntityType;"))
	private BlockEntityType<?> isHiveValid(BlockEntity blockEntity, Operation<BlockEntityType<?>> original) {
		BlockEntityType<?> type = original.call(blockEntity);
		if (Hooks.gadget && GadgetModule.BUZZY_CRAFTER_ENTITY.is(type)) {
			return BlockEntityType.BEEHIVE;
		}
		return type;
	}
}
