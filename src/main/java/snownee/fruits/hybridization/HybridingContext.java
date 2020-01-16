package snownee.fruits.hybridization;

import java.util.Collections;
import java.util.Set;

import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import snownee.fruits.Fruits;
import snownee.kiwi.crafting.EmptyInventory;

public class HybridingContext extends EmptyInventory {

    public final Set<Either<Fruits.Type, Block>> ingredients;

    public HybridingContext(Set<Either<Fruits.Type, Block>> ingredients) {
        this.ingredients = Collections.unmodifiableSet(ingredients);
    }
}
