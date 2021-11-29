package snownee.fruits.hybridization;

import java.util.Collection;
import java.util.Collections;

import com.mojang.datafixers.util.Either;

import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitType;
import snownee.kiwi.recipe.EmptyInventory;

public class HybridingContext extends EmptyInventory {

	public final Collection<Either<FruitType, Block>> ingredients;

	public HybridingContext(Collection<Either<FruitType, Block>> ingredients) {
		this.ingredients = Collections.unmodifiableCollection(ingredients);
	}
}
