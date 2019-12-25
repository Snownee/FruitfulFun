package snownee.fruits.hybridization;

import java.util.Set;

import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import snownee.fruits.Fruits;

// TODO
public class HybridingRecipe {
    public boolean matches(Set<Either<Fruits.Type, Block>> type) {
        return false;
    }

    public Fruits.Type getResult(Set<Either<Fruits.Type, Block>> type) {
        return null;
    }
}
