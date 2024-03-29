package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.bee.network.SSyncBeePacket;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "startSeenByPlayer", at = @At("HEAD"))
	private void startSeenByPlayer(ServerPlayer player, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if (entity instanceof Bee bee) {
			SSyncBeePacket.send(bee, player);
		}
	}

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> ci) {
		Entity entity = (Entity) (Object) this;
		if (entity instanceof Bee && source == entity.damageSources().wither() &&
				BeeAttributes.of(entity).hasTrait(Trait.WITHER_TOLERANT)) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "getPassengersRidingOffset", at = @At("HEAD"), cancellable = true)
	private void getPassengersRidingOffset(CallbackInfoReturnable<Double> ci) {
		Entity entity = (Entity) (Object) this;
		if (entity instanceof Bee) {
			ci.setReturnValue(entity.getBbHeight() * 0.6);
		}
	}
}
