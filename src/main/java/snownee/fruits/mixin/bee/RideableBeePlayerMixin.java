package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;

@Mixin(Player.class)
public class RideableBeePlayerMixin {
	@Inject(
			method = "checkRidingStatistics",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;getVehicle()Lnet/minecraft/world/entity/Entity;"),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true)
	private void checkRidingStatistics(double d, double e, double f, CallbackInfo ci, int i) {
		Player player = (Player) (Object) this;
		Entity vehicle = player.getVehicle();
		if (Hooks.bee && vehicle instanceof Bee) {
			player.awardStat(BeeModule.BEE_ONE_CM, i);
			ci.cancel();
		}
	}
}
