package snownee.fruits.mixin.test;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resources.ResourceKey;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.KiwiGO;

@Mixin(KiwiGO.class)
public class KiwiGOMixin {
	@Inject(method = "setKey", at = @At("HEAD"))
	private <T> void setKey(ResourceKey<T> key, CallbackInfo ci) {
		FruitfulFun.LOGGER.info("Registering {}", key);
	}
}
