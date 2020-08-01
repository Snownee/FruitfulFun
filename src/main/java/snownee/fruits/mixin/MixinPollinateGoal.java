package snownee.fruits.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;
import snownee.fruits.Hook;
import snownee.fruits.hybridization.Hybridization;

@Mixin(BeeEntity.PollinateGoal.class)
public abstract class MixinPollinateGoal extends BeeEntity.PassiveGoal {

    @Shadow(aliases = { "field_226491_b_", "b" })
    private BeeEntity this$0;

    MixinPollinateGoal(BeeEntity bee) {
        bee.super();
    }

    @Shadow
    private final Predicate<BlockState> flowerPredicate = Hook::canPollinate;

    @Inject(method = "resetTask", at = @At("HEAD"))
    public void onComplete(CallbackInfo cir) {
        if (Hybridization.INSTANCE == null || this$0.savedFlowerPos == null) {
            return;
        }
        Hook.onPollinateComplete(this$0);
    }
}
