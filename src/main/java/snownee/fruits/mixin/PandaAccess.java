package snownee.fruits.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.item.ItemEntity;

@Mixin(Panda.class)
public interface PandaAccess {

	@Accessor
	static Predicate<ItemEntity> getPANDA_ITEMS() {
		throw new AssertionError();
	}

	@Accessor
	static void setPANDA_ITEMS(Predicate<ItemEntity> value) {
		throw new AssertionError();
	}

}
