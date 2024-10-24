package snownee.fruits.mixin.food;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.ritual.RitualModule;

@Mixin(SkullBlockEntity.class)
public class SkullBlockEntityMixin {
	@Inject(method = "animation", at = @At("HEAD"), cancellable = true)
	private static void ritualAnimation(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			SkullBlockEntity skullBlockEntity,
			CallbackInfo ci) {
		if (blockState.is(Blocks.DRAGON_HEAD) || blockState.is(Blocks.DRAGON_WALL_HEAD)) {
			if (Hooks.food && RitualModule.tickDragonHead(level, blockPos, blockState, skullBlockEntity)) {
				ci.cancel();
			}
		}
	}
}
