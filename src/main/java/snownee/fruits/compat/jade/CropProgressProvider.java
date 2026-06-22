package snownee.fruits.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public class CropProgressProvider implements IBlockComponentProvider {
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (!config.get(JadeIds.MC_CROP_PROGRESS)) {
			return;
		}
		BlockState state = accessor.getBlockState();
		FruitLeavesBlock block = (FruitLeavesBlock) state.getBlock();
		if (!block.canGrow(state)) {
			return;
		}
		int age = state.getValue(FruitLeavesBlock.AGE);
		boolean needsPollination = false;
		if (FFCommonConfig.allogamousTrees && block.type.get().allogamous) {
			needsPollination = age == FruitLeavesBlock.BLOOMING;
		}
		addMaturityTooltip(tooltip, (age - 1) / 2.0F, needsPollination);
	}

	private static void addMaturityTooltip(ITooltip tooltip, float growthValue, boolean needsPollination) {
		growthValue *= 100.0F;
		if (growthValue < 100.0F) {
			MutableComponent component = Component.translatable(
					"tooltip.jade.crop_growth",
					IThemeHelper.get().info(String.format("%.0f%%", growthValue)));
			if (needsPollination) {
				component = Component.translatable("tip.fruitfulfun.cropNeedsPollination", component);
			}
			tooltip.add(component);
		} else {
			tooltip.add(Component.translatable(
					"tooltip.jade.crop_growth",
					IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"))));
		}
	}

	@Override
	public Identifier getUid() {
		return JadeCompat.CROP_PROGRESS;
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
