package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import snownee.fruits.bee.BeeModule;

@Mixin(AbstractVillager.class)
public class AbstractVillagerMixin {
	@Inject(method = "addOffersFromItemListings", at = @At("TAIL"))
	private static void addOffersFromItemListings(
			LootContext lootContext,
			MerchantOffers merchantOffers,
			HolderSet<VillagerTrade> potentialOffers,
			int numberOfOffers,
			CallbackInfo ci) {
		BeeModule.addBeekeeperTrades(merchantOffers, (AbstractVillager) (Object) this);
	}
}
