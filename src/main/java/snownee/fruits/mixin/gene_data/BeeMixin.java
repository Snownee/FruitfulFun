package snownee.fruits.mixin.gene_data;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.duck.FFBee;

@Mixin(Bee.class)
public abstract class BeeMixin implements FFBee {

	@Unique
	private final BeeAttributes beeAttributes = new BeeAttributes();

	@Override
	public BeeAttributes fruits$getBeeAttributes() {
		return beeAttributes;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		CompoundTag data = new CompoundTag();
		beeAttributes.toNBT(data);
		compoundTag.put("FruitfulFun", data);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		Bee bee = (Bee) (Object) this;
		compoundTag = compoundTag.getCompound("FruitfulFun");
		if (!compoundTag.contains("Genes")) {
			beeAttributes.randomize(bee);
		}
		beeAttributes.fromNBT(compoundTag, bee);
	}
}
