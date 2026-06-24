package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import snownee.fruits.CoreModule;
import snownee.fruits.Hooks;

@Mixin(InstrumentItem.class)
public class InstrumentItemMixin {

	@Inject(at = @At("HEAD"), method = "play")
	private static void play(Level level, Player player, Instrument instrument, CallbackInfo ci) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		HolderSet.Named<Instrument> holders = level.registryAccess()
				.lookupOrThrow(Registries.INSTRUMENT)
				.getOrThrow(CoreModule.HORN_HARVESTING_INSTRUMENT);
		if (holders.size() > 0 && holders.stream().anyMatch(holder -> holder.value() == instrument)) {
			Hooks.hornHarvest(serverPlayer);
		}
	}

}
