package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.duck.FFPlayer;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin implements FFPlayer {
	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		if (!fruits$isHaunting()) {
			return;
		}
		Entity entity = fruits$hauntingTarget();
		if (entity != null) {
			BeeModule.spawnEntityParticles(entity);
		}
	}
}
