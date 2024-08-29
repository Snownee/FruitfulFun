package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import snownee.fruits.bee.BeeModule;

@Mixin(Display.class)
public abstract class BeehiveDisplayMixin extends Entity {
	public BeehiveDisplayMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		Display self = (Display) (Object) this;
		if (BeeModule.isWaxedMarker(self)) {
			BeeModule.tickWaxedMarker(self);
		}
	}
}
