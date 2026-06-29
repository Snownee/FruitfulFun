package snownee.fruits.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.addon.vanilla.BeehiveProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public class CrafterProvider implements IBlockComponentProvider {
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (!config.get(JadeIds.MC_BEEHIVE)) {
			return;
		}
		tooltip.remove(JadeIds.MC_BEEHIVE);
		IThemeHelper t = IThemeHelper.get();
		Byte b = BeehiveProvider.INSTANCE.decodeFromData(accessor).orElse(null);
		if (b != null) {
			boolean full = b > 0;
			int bees = Math.abs(b);
			tooltip.add(Component.translatable("jade.beehive.bees", full ? t.success(bees) : t.info(bees)));
		}
	}

	@Override
	public Identifier getUid() {
		return JadeCompat.CRAFTER;
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
