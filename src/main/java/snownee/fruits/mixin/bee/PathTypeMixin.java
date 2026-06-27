package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.pathfinder.PathType;

@Mixin(PathType.class)
enum PathTypeMixin {
	FRUITFULFUN_LEAVES(-1);

	@Shadow
	PathTypeMixin(float defaultCost) {
	}
}
