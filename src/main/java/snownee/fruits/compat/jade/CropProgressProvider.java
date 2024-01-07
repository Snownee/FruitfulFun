package snownee.fruits.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public class CropProgressProvider implements IBlockComponentProvider {
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (!config.get(Identifiers.MC_CROP_PROGRESS)) {
			return;
		}
		int age = accessor.getBlockState().getValue(FruitLeavesBlock.AGE);
		if (age > 0) {
			addMaturityTooltip(tooltip, (age - 1) / 2.0F);
		}
	}

	private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
		growthValue *= 100.0F;
		if (growthValue < 100.0F) {
			tooltip.add(Component.translatable("tooltip.jade.crop_growth", IThemeHelper.get().info(String.format("%.0f%%", growthValue))));
		} else {
			tooltip.add(Component.translatable("tooltip.jade.crop_growth", IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"))));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.CROP_PROGRESS;
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
