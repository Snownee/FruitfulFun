package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.bee.network.SSyncBeePacket;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.duck.FFBee;

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal implements FFBee {

	@Shadow
	private int underWaterTicks;
	@Unique
	private final BeeAttributes beeAttributes = new BeeAttributes();
	@Unique
	private int rollTicks;

	public BeeMixin(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	@Shadow
	protected abstract void setHasStung(boolean bl);

	@Shadow
	private int timeSinceSting;

	@Override
	public BeeAttributes fruits$getBeeAttributes() {
		return beeAttributes;
	}

	@Override
	public void fruits$roll() {
		rollTicks = 6;
	}

	@Inject(at = @At("HEAD"), method = "isFlowerValid", cancellable = true)
	private void isFlowerValid(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (!Hooks.bee || !level().isLoaded(pos)) {
			return;
		}
		BlockState state = level().getBlockState(pos);
		if (!state.hasBlockEntity() && state.getBlock() instanceof FruitLeavesBlock) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		CompoundTag data = new CompoundTag();
		beeAttributes.toNBT(data);
		compoundTag.put("FruitfulFun", data);
		// used by BeeHiveBlockEntityMixin
		compoundTag.putBoolean("RainCapable", BeeAttributes.of(this).hasTrait(Trait.RAIN_CAPABLE));
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		Bee bee = (Bee) (Object) this;
		compoundTag = compoundTag.getCompound("FruitfulFun");
		if (!compoundTag.contains("Genes")) {
			beeAttributes.randomize(bee);
		}
		beeAttributes.fromNBT(compoundTag, bee);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Bee;updateRollAmount()V"))
	private void tick(CallbackInfo ci) {
		if (level().isClientSide || !Hooks.bee) {
			return;
		}
		Bee bee = (Bee) (Object) this;
		MobEffectInstance effect = bee.getEffect(BeeModule.MUTAGEN_EFFECT.get());
		BeeAttributes attributes = BeeAttributes.of(bee);
		long gameTime = level().getGameTime();
		attributes.setMutagenEndsIn(effect == null ? 0 : gameTime + effect.getDuration(), gameTime);
		if (beeAttributes.dirty) {
			beeAttributes.dirty = false;
			SSyncBeePacket.send(bee);
		}
		if (rollTicks > 0) {
			setRolling(--rollTicks != 0);
		}
	}

	@Inject(method = "customServerAiStep", at = @At("HEAD"))
	private void customServerAiStep(CallbackInfo ci) {
		if (!Hooks.bee) {
			return;
		}
		if (underWaterTicks >= 20) {
			ejectPassengers();
		}
		if (hasStung() && BeeAttributes.of(this).hasTrait(Trait.WARRIOR)) {
			if (timeSinceSting == 0) {
				hurt(damageSources().generic(), 4);
			} else if (!isDeadOrDying() && tickCount % 10 == 0 && random.nextInt(20) == 0) {
				setHasStung(false);
				timeSinceSting = 0;
			} else {
				timeSinceSting = 3; // do not execute the death logic that runs every 5 ticks
			}
		}
		if (!isDeadOrDying() && getHealth() < getMaxHealth()) {
			int healingInterval = FFCommonConfig.beeNaturalHealingInterval;
			if (healingInterval > 0 && random.nextInt(healingInterval) == 0) {
				heal(1.0F);
			}
		}
	}

	@Shadow
	protected abstract void setRolling(boolean bl);

	@Shadow
	public abstract boolean hasStung();

	@Shadow
	int ticksWithoutNectarSinceExitingHive;

	@ModifyExpressionValue(
			method = "wantsToEnterHive",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private boolean wantsToEnterHive(boolean original) {
		return original && !BeeAttributes.of(this).hasTrait(Trait.RAIN_CAPABLE);
	}

	@Inject(method = "isTiredOfLookingForNectar", at = @At("HEAD"), cancellable = true)
	private void isTiredOfLookingForNectar(CallbackInfoReturnable<Boolean> cir) {
		if (ticksWithoutNectarSinceExitingHive > 1800 && BeeAttributes.of(this).hasTrait(Trait.LAZY)) {
			cir.setReturnValue(true);
		}
	}
}
