package snownee.fruits.mixin.gene_data;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import snownee.fruits.bee.BeeAttributes;

@Mixin(Mob.class)
public class MobMixin {
	@Inject(method = "onOffspringSpawnedFromEgg", at = @At("HEAD"))
	private void onOffspringSpawnedFromEgg(Player spawner, Mob offspring, CallbackInfo ci) {
		if (offspring instanceof Bee) {
			BeeAttributes.of(offspring).addTrusted(spawner.getUUID());
		}
	}

	@Inject(method = "finalizeSpawn", at = @At("HEAD"))
	private void finalizeSpawn(
			ServerLevelAccessor level,
			DifficultyInstance difficulty,
			EntitySpawnReason spawnReason,
			SpawnGroupData groupData,
			CallbackInfoReturnable<SpawnGroupData> cir) {
		Mob mob = (Mob) (Object) this;
		if (mob instanceof Bee bee) {
			BeeAttributes.of(bee).randomize(bee);
		}
	}
}
