package snownee.fruits.mixin.bee;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Trait;
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

	@WrapOperation(
			method = "releaseOccupant",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
	private static Object releaseOccupantSuppressDefault(
			EnvironmentAttributeSystem instance,
			EnvironmentAttribute<Boolean> environmentAttribute,
			BlockPos blockPos,
			Operation<Object> original,
			@Local(argsOnly = true) BeehiveBlockEntity.Occupant beeData) {
		return ((Boolean) original.call(instance, environmentAttribute, blockPos)) && !beeData.entityData().contains("RainCapable");
	}

	@Mixin(BeehiveBlockEntity.Occupant.class)
	public static class OccupantMixin {
		@Inject(
				method = "of", at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/nbt/CompoundTag;getBooleanOr(Ljava/lang/String;Z)Z"))
		private static void addRainCapableMark(
				Entity entity,
				CallbackInfoReturnable<BeehiveBlockEntity.Occupant> cir,
				@Local(name = "entityTag") CompoundTag entityTag) {
			if (entity instanceof Bee bee && BeeAttributes.of(bee).hasTrait(Trait.RAIN_CAPABLE)) {
				entityTag.putBoolean("RainCapable", true);
			}
		}
	}

	@Inject(method = "serverTick", at = @At("HEAD"))
	private static void serverTick(
			Level level,
			BlockPos blockPos,
			BlockState state,
			BeehiveBlockEntity entity,
			CallbackInfo ci) {
		BeehiveBlockEntityMixin self = (BeehiveBlockEntityMixin) (Object) entity;
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
			BlockPos pos,
			BlockState state,
			List<BeehiveBlockEntity.BeeData> stored,
			@Nullable BlockPos savedFlowerPos,
			Operation<Void> original,
			@Local(argsOnly = true) BeehiveBlockEntity beehive) {
		FFBeehiveBlockEntity self = (FFBeehiveBlockEntity) beehive;
		if (!self.fruits$isWaxed()) {
			original.call(level, pos, state, stored, savedFlowerPos);
		}
	}

	@WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	private static boolean serverTick_playWorkingSound(
			List<BeehiveBlockEntity.BeeData> list,
			Operation<Boolean> original,
			@Local(argsOnly = true) BeehiveBlockEntity beehive) {
		FFBeehiveBlockEntity self = (FFBeehiveBlockEntity) beehive;
		if (!self.fruits$isWaxed()) {
			return original.call(list);
		}
		return true;
	}

	@Inject(method = "loadAdditional", at = @At("TAIL"))
	private void load(ValueInput input, CallbackInfo ci) {
		waxedTicks = input.getIntOr("FruitfulFun:WaxedTicks", 0);
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	private void saveAdditional(ValueOutput output, CallbackInfo ci) {
		if (fruits$isWaxed()) {
			output.putInt("FruitfulFun:WaxedTicks", waxedTicks);
		}
	}

	@Override
	public boolean fruits$isWaxed() {
		if (waxedTicks > 0) {
			return true;
		}
		if (level != null && level.isClientSide()) {
			return !fruits$findWaxedMarkers().isEmpty();
		}
		return false;
	}

	@Override
	public void fruits$setWaxed(boolean waxed) {
		waxedTicks = waxed ? BeeModule.WAXED_TICKS : 0;
		Objects.requireNonNull(level);
		if (waxed) {
			Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
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
