package snownee.fruits.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public class CrafterProvider implements IBlockComponentProvider {
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (!config.get(Identifiers.MC_BEEHIVE)) {
			return;
		}
		tooltip.remove(Identifiers.MC_BEEHIVE);
		IThemeHelper t = IThemeHelper.get();
		if (accessor.getServerData().contains("Full")) {
			boolean full = accessor.getServerData().getBoolean("Full");
			int bees = accessor.getServerData().getByte("Bees");
			tooltip.add(Component.translatable("jade.beehive.bees", full ? t.success(bees) : t.info(bees)));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.CRAFTER;
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
