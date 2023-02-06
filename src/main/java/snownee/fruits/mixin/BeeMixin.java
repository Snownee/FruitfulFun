package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FilteredFlyingPathNavigation;
import snownee.fruits.Hooks;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {

	public BeeMixin(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	@Inject(at = @At("HEAD"), method = "isFlowerValid", cancellable = true)
	private void fruits_isFlowerValid(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (!Hooks.hybridization || !level.isLoaded(pos)) {
			return;
		}
		BlockState state = level.getBlockState(pos);
		if (!state.hasBlockEntity() && state.getBlock() instanceof FruitLeavesBlock) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "createNavigation", cancellable = true)
	private void fruits_createNavigation(Level levelIn, CallbackInfoReturnable<PathNavigation> cir) {
		FilteredFlyingPathNavigation flyingpathnavigator = new FilteredFlyingPathNavigation(this, levelIn);
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanFloat(false);
		flyingpathnavigator.setCanPassDoors(true);
		cir.setReturnValue(flyingpathnavigator);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return (level.getDifficulty() == Difficulty.EASY && source == DamageSource.WITHER) || super.isInvulnerableTo(source);
	}

}
