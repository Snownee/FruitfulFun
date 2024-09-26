package snownee.fruits.duck;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import snownee.fruits.bee.genetics.Trait;

public interface FFLivingEntity {
	@Nullable
	Player fruits$getHauntedBy();

	void fruits$setHauntedBy(@Nullable UUID uuid);

	boolean fruit$hasHauntedTrait(Trait trait);
}
