package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;

@Mixin(Animal.class)
public class AnimalMixin {

	@Inject(method = "finalizeSpawnChildFromBreeding", at = @At("HEAD"))
	private void finalizeSpawnChildFromBreeding(ServerLevel serverLevel, Animal animal, AgeableMob ageableMob, CallbackInfo ci) {
		Animal self = (Animal) (Object) this;
		if (self instanceof Bee parent1 && animal instanceof Bee parent2 && ageableMob instanceof Bee baby) {
			Hooks.spawnBeeFromBreeding(parent1, parent2, baby);
		}
	}

	@Inject(method = "canFallInLove", at = @At("HEAD"), cancellable = true)
	private void canFallInLove(CallbackInfoReturnable<Boolean> cir) {
		Animal self = (Animal) (Object) this;
		if (self instanceof Bee && BeeAttributes.of(self).hasTrait(Trait.GHOST)) {
			cir.setReturnValue(false);
		}
	}

}
