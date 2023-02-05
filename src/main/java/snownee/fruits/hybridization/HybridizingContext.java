package snownee.fruits.hybridization;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.world.level.block.Block;
import snownee.kiwi.recipe.EmptyInventory;

public class HybridizingContext extends EmptyInventory {

	public final Collection<Block> ingredients;

	public HybridizingContext(Collection<Block> ingredients) {
		this.ingredients = Collections.unmodifiableCollection(ingredients);
	}
}
