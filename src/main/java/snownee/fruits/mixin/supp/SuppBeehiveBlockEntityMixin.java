package snownee.fruits.mixin.supp;

import org.spongepowered.asm.mixin.Mixin;

import net.mehvahdjukaar.supplementaries.common.block.IWaxable;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(BeehiveBlockEntity.class)
public abstract class SuppBeehiveBlockEntityMixin implements IWaxable, FFBeehiveBlockEntity {
	@Override
	public boolean isWaxed() {
		return fruits$isWaxed();
	}

	@Override
	public void setWaxed(boolean b) {
		fruits$setWaxed(b);
	}
}
