package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.Hooks;

@Mixin(Animal.class)
public class AnimalMixin {

	@Inject(method = "finalizeSpawnChildFromBreeding", at = @At("HEAD"))
	private void fruits_finalizeSpawnChildFromBreeding(ServerLevel serverLevel, Animal animal, AgeableMob ageableMob, CallbackInfo ci) {
		Animal self = (Animal) (Object) this;
		if (self instanceof Bee parent1 && animal instanceof Bee parent2 && ageableMob instanceof Bee baby) {
			Hooks.spawnBeeFromBreeding(parent1, parent2, baby);
		}
	}

}
