package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.component.CustomData;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;

@Mixin(GiveGiftToHero.class)
public class GiveGiftToHeroMixin {

	@Inject(at = @At("HEAD"), method = "throwGift", cancellable = true)
	private void getItemToThrow(ServerLevel level, Villager villager, LivingEntity target, CallbackInfo ci) {
		if (FFCommonConfig.appleSaplingFromHeroOfTheVillage && villager.isBaby()) {
			CustomData data = villager.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
			CompoundTag compoundTag = data.copyTag();
			if (compoundTag.getBooleanOr("FFGiftedAppleSapling", false)) {
				return;
			}
			BehaviorUtils.throwItem(villager, CoreModule.APPLE_SAPLING.itemStack(), target.position());
			compoundTag.putBoolean("FFGiftedAppleSapling", true);
			villager.setComponent(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
			ci.cancel();
		}
	}

}
