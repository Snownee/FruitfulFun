package snownee.fruits.mixin.crafter;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(Bee.class)
public class BeeMixin {
	@WrapOperation(
			method = "getBeehiveBlockEntity", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Ljava/util/Optional;"),
			require = 0)
	private Optional<? extends BeehiveBlockEntity> getBeehiveBlockEntity(
			Level level,
			BlockPos blockPos,
			BlockEntityType<BeehiveBlockEntity> blockEntityType,
			Operation<Optional<? extends BeehiveBlockEntity>> original) {
		Optional<? extends BeehiveBlockEntity> blockEntity = original.call(level, blockPos, blockEntityType);
		if (Hooks.gadget && blockEntity.isEmpty()) {
			return level.getBlockEntity(blockPos, GadgetModule.BUZZY_CRAFTER_ENTITY.get());
		}
		return blockEntity;
	}
}
