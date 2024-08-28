package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;
import snownee.fruits.bee.BeeModule;

@Mixin(AbstractVillager.class)
public class AbstractVillagerMixin {
	@Inject(method = "addOffersFromItemListings", at = @At("TAIL"))
	private void addOffersFromItemListings(
			MerchantOffers merchantOffers,
			VillagerTrades.ItemListing[] itemListings,
			int i,
			CallbackInfo ci) {
		BeeModule.addBeekeeperTrades(merchantOffers, (AbstractVillager) (Object) this);
	}
}
