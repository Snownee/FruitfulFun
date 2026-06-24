package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFLivingEntity;

@Mixin(Mob.class)
public class RideableBeeMobMixin {
	@Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
	private void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
		Mob mob = (Mob) (Object) this;
		if (Hooks.bee && !mob.isNoAi() && mob instanceof Bee bee) {
			BeeAttributes attributes = BeeAttributes.of(bee);
			if (bee.isSaddled() && bee.getFirstPassenger() instanceof Player player &&
					(player.isCreative() || attributes.trusts(player.getUUID()))) {
				cir.setReturnValue(player);
			}
			if (attributes.hasTrait(Trait.GHOST)) {
				Player player = ((FFLivingEntity) bee).fruits$getHauntedBy();
				if (player != null) {
					cir.setReturnValue(player);
				}
			}
		}
	}

	@Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
	private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci) {
		Mob mob = (Mob) (Object) this;
		if (Hooks.bee && mob instanceof Bee bee && !bee.isDeadOrDying()) {
			InteractionResult result = Hooks.playerInteractBee(player, hand, bee);
			if (result.consumesAction()) {
				ci.setReturnValue(result);
			}
		}
	}

	@Inject(method = "canDispenserEquipIntoSlot", at = @At("HEAD"), cancellable = true)
	protected void canDispenserEquipIntoSlot(final EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
		Mob mob = (Mob) (Object) this;
		if (Hooks.bee && slot == EquipmentSlot.SADDLE && mob instanceof Bee) {
			cir.setReturnValue(true);
		}
	}
}
