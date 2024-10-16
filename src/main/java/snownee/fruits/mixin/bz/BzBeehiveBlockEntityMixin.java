package snownee.fruits.mixin.bz;

import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;

@Mixin(BeehiveBlockEntity.class)
public abstract class BzBeehiveBlockEntityMixin extends BlockEntity {
	public BzBeehiveBlockEntityMixin(
			BlockEntityType<?> blockEntityType,
			BlockPos blockPos,
			BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	@Inject(method = "addOccupantWithPresetTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;stopRiding()V"))
	private void addOccupantWithPresetTicks(Entity occupant, boolean hasNectar, int ticksInHive, CallbackInfo ci) {
		if (!Hooks.bee || !(((FFLivingEntity) occupant).fruits$getHauntedBy() instanceof ServerPlayer player)) {
			return;
		}
		HauntingManager manager = ((FFPlayer) player).fruits$hauntingManager();
		if (manager == null) {
			return;
		}
		manager.getExorcised(player);
		try {
			Class<?> clazz = Class.forName("com.telepathicgrunt.the_bumblezone.modcompat.BumblezoneAPI");
			Method method = clazz.getDeclaredMethod("runGenericTeleport", Player.class, BlockPos.class);
			method.invoke(null, player, getBlockPos());
		} catch (Exception e) {
			FruitfulFun.LOGGER.error("Failed to teleport player to the Bumblezone", e);
		}
	}
}
