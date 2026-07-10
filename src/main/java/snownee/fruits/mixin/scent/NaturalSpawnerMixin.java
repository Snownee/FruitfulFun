package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
	@Inject(method = "spawnCategoryForChunk", at = @At("HEAD"), cancellable = true)
	private static void spawnCategoryForChunk(
			MobCategory mobCategory,
			ServerLevel level,
			LevelChunk chunk,
			NaturalSpawner.SpawnPredicate extraTest,
			NaturalSpawner.AfterSpawnCallback spawnCallback,
			CallbackInfo ci) {
		if (mobCategory == MobCategory.MONSTER || mobCategory == MobCategory.AMBIENT) {
			if (Hooks.gadget && GadgetModule.PEACE.get().isActiveAt(chunk)) {
				ci.cancel();
			}
		}
	}
}
