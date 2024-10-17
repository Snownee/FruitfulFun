package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
	@Inject(method = "entityInside", at = @At("HEAD"))
	private void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
		Player hauntedBy;
		if (Hooks.bee && !level.isClientSide && state.is(Blocks.SOUL_FIRE) && entity instanceof FFLivingEntity living &&
				(hauntedBy = living.fruits$getHauntedBy()) != null) {
			HauntingManager hauntingManager = FFPlayer.of(hauntedBy).fruits$hauntingManager();
			if (hauntingManager != null) {
				hauntingManager.getExorcised((ServerPlayer) hauntedBy);
			}
		}
	}
}
