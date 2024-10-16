package snownee.fruits.duck;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;

public interface FFLivingEntity {
	@Nullable
	Player fruits$getHauntedBy();

	void fruits$setHauntedBy(@Nullable UUID uuid);
}
