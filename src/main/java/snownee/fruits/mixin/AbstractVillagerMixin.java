package snownee.fruits.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;

@Mixin(AbstractVillager.class)
public class AbstractVillagerMixin {
	@Shadow
	@Nullable
	protected MerchantOffers offers;

	@Inject(
			method = "getOffers", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;updateTrades(Lnet/minecraft/server/level/ServerLevel;)V"))
	private void addOffersFromItemListings(CallbackInfoReturnable<MerchantOffers> cir) {
		if (offers != null) {
			AbstractVillager villager = (AbstractVillager) (Object) this;
			if (Hooks.bee && FFCommonConfig.beehiveTrade) {
				BeeModule.addBeekeeperTrades(offers, villager);
			}
			if (FFCommonConfig.wanderingTraderSaplingPrice > 0 && villager instanceof WanderingTrader trader) {
				CoreModule.addWanderingTraderTrades(offers, trader);
			}
		}
	}
}
