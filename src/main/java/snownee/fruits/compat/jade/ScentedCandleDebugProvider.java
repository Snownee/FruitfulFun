package snownee.fruits.compat.jade;

import com.ibm.icu.text.NumberFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.fruits.FruitfulFun;
import snownee.fruits.gadget.BuzzyPowerStorage;
import snownee.fruits.gadget.scent.ScentedCandleBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class ScentedCandleDebugProvider implements IServerDataProvider<BlockAccessor> {
	private static final Identifier UID = FruitfulFun.id("scented_candle_debug");

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		if (accessor.getBlockEntity() instanceof ScentedCandleBlockEntity be) {
			data.put("power", be.power().save());
		}
	}

	@Override
	public Identifier getUid() {
		return UID;
	}

	public static class Client extends ScentedCandleDebugProvider implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			BuzzyPowerStorage power = accessor.getServerData()
					.read("power", BuzzyPowerStorage.CODEC)
					.orElse(null);
			if (power == null) {
				return;
			}
			NumberFormat format = NumberFormat.getNumberInstance();
			tooltip.add(Component.literal("%s %s %s".formatted(
					ChatFormatting.RED + format.format(power.red()),
					ChatFormatting.GREEN + format.format(power.green()),
					ChatFormatting.BLUE + format.format(power.blue()))));
			tooltip.add(Component.literal("%s / %s".formatted(
					format.format(power.life()),
					format.format(power.maxLife()))));
		}

		@Override
		public boolean isRequired() {
			return true;
		}
	}
}
