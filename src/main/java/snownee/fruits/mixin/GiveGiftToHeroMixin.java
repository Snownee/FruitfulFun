package snownee.fruits.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitsConfig;

@Mixin(GiveGiftToHero.class)
public class GiveGiftToHeroMixin {

	@Inject(at = @At("HEAD"), method = "getItemToThrow", cancellable = true)
	private void fruits_getItemToThrow(Villager villager, CallbackInfoReturnable<List<ItemStack>> ci) {
		if (FruitsConfig.appleSaplingFromHeroOfTheVillage && villager.isBaby()) {
			ci.setReturnValue(List.of(CoreModule.APPLE_SAPLING.itemStack()));
		}
	}

}
