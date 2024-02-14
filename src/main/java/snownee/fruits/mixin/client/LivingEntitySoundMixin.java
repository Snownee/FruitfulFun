package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.vacuum.VacModule;
import snownee.fruits.vacuum.client.VacGunSoundInstance;

@Mixin(LivingEntity.class)
public class LivingEntitySoundMixin {
	@Inject(method = "startUsingItem", at = @At("TAIL"))
	private void startUsingItem(InteractionHand interactionHand, CallbackInfo ci) {
		if (!Hooks.vac) {
			return;
		}
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!entity.level().isClientSide || !(entity instanceof Player player) || !VacModule.VAC_GUN.is(player.getUseItem())) {
			return;
		}
		Minecraft.getInstance().getSoundManager().play(new VacGunSoundInstance(player));
	}
}
