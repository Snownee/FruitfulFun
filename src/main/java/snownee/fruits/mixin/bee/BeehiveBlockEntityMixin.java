package snownee.fruits.mixin.bee;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFBee;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin extends BlockEntity implements FFBeehiveBlockEntity {
	@Unique
	private int waxedTicks;

	public BeehiveBlockEntityMixin(
			BlockEntityType<?> blockEntityType,
			BlockPos blockPos,
			BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	@WrapOperation(method = "releaseOccupant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private static boolean releaseOccupantSuppressDefault(
			Level instance,
			Operation<Boolean> original,
			@Local(argsOnly = true) BeehiveBlockEntity.BeeData beeData) {
		return original.call(instance) && !beeData.entityData.getBoolean("RainCapable");
	}

	@WrapOperation(
			method = "addOccupantWithPresetTicks", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;storeBee(Lnet/minecraft/nbt/CompoundTag;IZ)V"))
	private void addOccupantWithPresetTicks(
			BeehiveBlockEntity instance,
			CompoundTag compoundTag,
			int i,
			boolean bl,
			Operation<Void> original,
			@Local(argsOnly = true) Entity entity) {
		if (entity instanceof FFBee && BeeAttributes.of(entity).hasTrait(Trait.RAIN_CAPABLE)) {
			compoundTag.putBoolean("RainCapable", true);
		}
	}

	@Inject(method = "serverTick", at = @At("HEAD"))
	private static void serverTick(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			@NotNull BeehiveBlockEntity beehiveBlockEntity,
			CallbackInfo ci) {
		BeehiveBlockEntityMixin self = (BeehiveBlockEntityMixin) (Object) beehiveBlockEntity;
		if (self.waxedTicks > 0) {
			self.waxedTicks--;
		}
	}

	@WrapOperation(
			method = "serverTick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;tickOccupants(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/List;Lnet/minecraft/core/BlockPos;)V"))
	private static void serverTick_tickOccupants(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			List<BeehiveBlockEntity.BeeData> list,
			@Nullable BlockPos blockPos2,
			Operation<Void> original,
			@Local(argsOnly = true) @NotNull BeehiveBlockEntity beehive) {
		FFBeehiveBlockEntity self = (FFBeehiveBlockEntity) beehive;
		if (!self.fruits$isWaxed()) {
			original.call(level, blockPos, blockState, list, blockPos2);
		}
	}

	@WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	private static boolean serverTick_playWorkingSound(
			List<BeehiveBlockEntity.BeeData> list,
			Operation<Boolean> original,
			@Local(argsOnly = true) @NotNull BeehiveBlockEntity beehive) {
		FFBeehiveBlockEntity self = (FFBeehiveBlockEntity) beehive;
		if (!self.fruits$isWaxed()) {
			return original.call(list);
		}
		return true;
	}

	@Inject(method = "load", at = @At("TAIL"))
	private void load(CompoundTag compoundTag, CallbackInfo ci) {
		waxedTicks = compoundTag.getInt("FruitfulFun:WaxedTicks");
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	private void saveAdditional(CompoundTag compoundTag, CallbackInfo ci) {
		if (fruits$isWaxed()) {
			compoundTag.putInt("FruitfulFun:WaxedTicks", waxedTicks);
		}
	}

	@Override
	public boolean fruits$isWaxed() {
		if (waxedTicks > 0) {
			return true;
		}
		if (Objects.requireNonNull(level).isClientSide) {
			return !fruits$findWaxedMarkers().isEmpty();
		}
		return false;
	}

	@Override
	public void fruits$setWaxed(boolean waxed) {
		waxedTicks = waxed ? BeeModule.WAXED_TICKS : 0;
		Objects.requireNonNull(level);
		if (waxed) {
			Display.BlockDisplay display = Objects.requireNonNull(EntityType.BLOCK_DISPLAY.create(level));
			display.setPos(Vec3.atCenterOf(getBlockPos()));
			display.setCustomName(Component.literal(BeeModule.WAXED_MARKER_NAME));
			level.addFreshEntity(display);
		} else {
			fruits$findWaxedMarkers().forEach(Entity::discard);
		}
	}

	@Override
	public List<Display.BlockDisplay> fruits$findWaxedMarkers() {
		return Objects.requireNonNull(level).getEntities(EntityType.BLOCK_DISPLAY, new AABB(getBlockPos()), BeeModule::isWaxedMarker);
	}
}
