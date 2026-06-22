package snownee.fruits.mixin.bee;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.crafting.Ingredient;
import snownee.fruits.Hooks;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.util.CommonProxy;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {
	@Unique
	private boolean isBee;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(PathfinderMob pathfinderMob, double d, Ingredient ingredient, boolean bl, CallbackInfo ci) {
		isBee = Hooks.bee && pathfinderMob instanceof Bee;
	}

	@Inject(method = "shouldFollow", at = @At("HEAD"), cancellable = true)
	private void shouldFollow(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!isBee) {
			return;
		}
		if (CommonProxy.getFlowerCrown(entity) != null || Stream.of(entity.getMainHandItem(), entity.getOffhandItem())
				.anyMatch(i -> i.getItem() instanceof FlowerCrownItem)) {
			cir.setReturnValue(true);
		}
	}
}
