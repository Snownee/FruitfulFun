package snownee.fruits.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Strings;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;

@Mixin(InstrumentItem.class)
public class InstrumentItemMixin {

	@Inject(at = @At("HEAD"), method = "play")
	private static void play(Level level, Player player, Instrument instrument, CallbackInfo ci) {
		if (level.isClientSide || Strings.isNullOrEmpty(FFCommonConfig.hornHarvestingInstrument)) {
			return;
		}
		if (FFCommonConfig.hornHarvestingInstrument.equals(Objects.toString(BuiltInRegistries.INSTRUMENT.getKey(instrument)))) {
			Hooks.hornHarvest((ServerLevel) level, (ServerPlayer) player);
		}
	}

}
