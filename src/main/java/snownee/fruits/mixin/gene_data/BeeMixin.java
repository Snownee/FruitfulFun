package snownee.fruits.mixin.gene_data;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.duck.FFBee;

@Mixin(Bee.class)
public abstract class BeeMixin implements FFBee {

	@Unique
	private BeeAttributes beeAttributes = new BeeAttributes();

	@Override
	public BeeAttributes fruits$getBeeAttributes() {
		return beeAttributes;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
		output.store("FruitfulFun", BeeAttributes.CODEC, beeAttributes);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
		Bee bee = (Bee) (Object) this;
		Optional<BeeAttributes> attributes = input.read("FruitfulFun", BeeAttributes.CODEC);
		if (attributes.isPresent()) {
			beeAttributes = attributes.get();
			beeAttributes.updateTraits(bee);
		} else {
			beeAttributes.randomize(bee);
		}
	}
}
