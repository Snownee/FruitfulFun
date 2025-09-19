package snownee.fruits.mixin.crafter;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.animal.Bee;

@Mixin(Bee.class)
public class BeeMixin {
	// Forge has patched it
/*	@WrapOperation(
			method = "isHiveValid", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getType()Lnet/minecraft/world/level/block/entity/BlockEntityType;"))
	private BlockEntityType<?> isHiveValid(BlockEntity blockEntity, Operation<BlockEntityType<?>> original) {
		BlockEntityType<?> type = original.call(blockEntity);
		if (Hooks.gadget && GadgetModule.BUZZY_CRAFTER_ENTITY.is(type)) {
			return BlockEntityType.BEEHIVE;
		}
		return type;
	}*/
}
