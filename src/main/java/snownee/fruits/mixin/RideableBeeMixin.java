package snownee.fruits.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Items;
import snownee.fruits.bee.BeeAttributes;

@Mixin(Bee.class)
public class RideableBeeMixin implements Saddleable {
	@Override
	public boolean isSaddleable() {
		Bee bee = (Bee) (Object) this;
		return bee.isAlive() && !bee.isBaby() && BeeAttributes.of(bee).isSaddleable();
	}

	@Override
	public void equipSaddle(@Nullable SoundSource soundSource) {
		BeeAttributes.of(this).setSaddle(Items.SADDLE.getDefaultInstance());
	}

	@Override
	public boolean isSaddled() {
		return BeeAttributes.of(this).isSaddled();
	}

	@Inject(method = "wantsToEnterHive", at = @At("HEAD"), cancellable = true)
	private void fruits_wantsToEnterHive(CallbackInfoReturnable<Boolean> cir) {
		Bee bee = (Bee) (Object) this;
		if (bee.isVehicle()) {
			cir.setReturnValue(false);
		}
	}
}
