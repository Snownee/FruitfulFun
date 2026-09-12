package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
		RegistryLookup<Instrument> instruments = level.registryAccess().lookupOrThrow(Registries.INSTRUMENT);
		if (matches(instruments, CoreModule.HORN_HARVESTING_INSTRUMENT, instrument)) {
			Hooks.hornHarvest(serverPlayer);
		}
		if (matches(instruments, CoreModule.HORN_BEE_RETURN_INSTRUMENT, instrument)) {
			Hooks.hornCallBees(serverPlayer);
		}
	}

	@Unique
	private static boolean matches(RegistryLookup<Instrument> instruments, TagKey<Instrument> tag, Instrument instrument) {
		return instruments.getOrThrow(tag).stream().anyMatch($ -> $.value() == instrument);
	}

}
