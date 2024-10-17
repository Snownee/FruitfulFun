package snownee.fruits.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import snownee.fruits.duck.FFPlayer;

@Mixin(value = ForgeGui.class, remap = false)
public abstract class ForgeGuiMixin {
	@Shadow
	public abstract Minecraft getMinecraft();

	@WrapOperation(
			method = "renderExperience",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"), remap = true)
	private boolean hasExperience(MultiPlayerGameMode instance, Operation<Boolean> original) {
		if (getMinecraft().player != null && FFPlayer.of(getMinecraft().player).fruits$isHaunting()) {
			return false;
		}
		return original.call(instance);
	}
}
