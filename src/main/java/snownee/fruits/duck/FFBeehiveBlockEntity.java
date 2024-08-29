package snownee.fruits.duck;

import java.util.List;

import net.minecraft.world.entity.Display;

public interface FFBeehiveBlockEntity {
	boolean fruits$isWaxed();

	void fruits$setWaxed(boolean waxed);

	List<Display.BlockDisplay> fruits$findWaxedMarkers();
}
