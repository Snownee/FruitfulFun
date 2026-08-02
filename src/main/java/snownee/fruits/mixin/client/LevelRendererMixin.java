package snownee.fruits.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.Hooks;
import snownee.fruits.block.SlidingDoorBlock;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	private @Nullable ClientLevel level;

	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(
			method = "renderHitOutline",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
	public VoxelShape slidingDoorOutline(
			BlockState blockState,
			BlockGetter blockGetter,
			BlockPos pos,
			CollisionContext collisionContext,
			Operation<VoxelShape> original) {
		if (level != null && blockState.getBlock() instanceof SlidingDoorBlock) {
			VoxelShape shape = Hooks.handleSlidingDoorOutline(blockState, level, pos, minecraft.getFrameTime());
			if (!shape.isEmpty()) {
				return shape;
			}
		}
		return original.call(blockState, blockGetter, pos, collisionContext);
	}
}
