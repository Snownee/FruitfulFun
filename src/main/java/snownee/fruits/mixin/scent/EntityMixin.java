package snownee.fruits.mixin.scent;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(value = Entity.class, priority = 600)
public abstract class EntityMixin {
	@Shadow
	public abstract Level level();

	@Shadow
	public abstract BlockPos blockPosition();

	@Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
	private void teleportTo(double x, double y, double z, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if (Hooks.gadget && entity instanceof LivingEntity && GadgetModule.ENDER.get().isActiveAt(Objects.requireNonNull(level())
				.getChunkAt(blockPosition()))) {
			ci.cancel();
		}
	}
}
