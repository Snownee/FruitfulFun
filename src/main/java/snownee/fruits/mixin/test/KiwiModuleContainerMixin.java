package snownee.fruits.mixin.test;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import snownee.fruits.FruitfulFun;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModuleContainer;

@Mixin(KiwiModuleContainer.class)
public class KiwiModuleContainerMixin {
	@Shadow
	@Final
	public AbstractModule module;

	@Inject(method = "loadGameObjects", at = @At("HEAD"))
	private void loadGameObjects(CallbackInfo ci) {
		FruitfulFun.LOGGER.info("Loading game objects: {}", module.uid);
	}
}
