package snownee.fruits.mixin.bee;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.util.CommonProxy;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {
	@Unique
	private boolean isBee;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Mob;DLjava/util/function/Predicate;ZD)V", at = @At("RETURN"))
	private void init(Mob mob, double speedModifier, Predicate<ItemStack> items, boolean canScare, double stopDistance, CallbackInfo ci) {
		isBee = Hooks.bee && mob instanceof Bee;
	}

	@Inject(method = "shouldFollow", at = @At("HEAD"), cancellable = true)
	private void shouldFollow(LivingEntity player, CallbackInfoReturnable<Boolean> cir) {
		if (!isBee) {
			return;
		}
		if (CommonProxy.getFlowerCrown(player) != null || Stream.of(player.getMainHandItem(), player.getOffhandItem())
				.anyMatch(i -> i.getItem() instanceof FlowerCrownItem)) {
			cir.setReturnValue(true);
		}
	}
}
