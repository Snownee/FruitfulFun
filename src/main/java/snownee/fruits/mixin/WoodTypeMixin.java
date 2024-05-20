package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.block.state.properties.WoodType;
import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;

@Mixin(WoodType.class)
public abstract class WoodTypeMixin {
	@Shadow
	public static WoodType register(WoodType woodType) {
		throw new AssertionError();
	}

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void initWoodTypes(CallbackInfo ci) {
		register(CoreModule.CITRUS_WOOD_TYPE);
		register(CherryModule.REDLOVE_WOOD_TYPE);
	}
}
