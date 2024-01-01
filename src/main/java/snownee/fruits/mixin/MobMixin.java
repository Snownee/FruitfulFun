package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import snownee.fruits.bee.BeeAttributes;

@Mixin(Mob.class)
public class MobMixin {
	@Inject(method = "onOffspringSpawnedFromEgg", at = @At("HEAD"))
	private void onOffspringSpawnedFromEgg(Player player, Mob mob, CallbackInfo ci) {
		if (mob instanceof Bee) {
			BeeAttributes.of(mob).addTrusted(player.getUUID());
		}
	}

	@Inject(method = "finalizeSpawn", at = @At("HEAD"))
	private void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
		Mob mob = (Mob) (Object) this;
		if (mob instanceof Bee bee) {
			BeeAttributes.of(bee).randomize(bee);
		}
	}
}
