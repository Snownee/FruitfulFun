package snownee.fruits.hybridization;

import java.util.Collections;
import java.util.Set;

import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import snownee.fruits.FruitType;
import snownee.kiwi.crafting.EmptyInventory;

public class HybridingContext extends EmptyInventory {

    public final Set<Either<FruitType, Block>> ingredients;

    public HybridingContext(Set<Either<FruitType, Block>> ingredients) {
        this.ingredients = Collections.unmodifiableSet(ingredients);
    }
}
