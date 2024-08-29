package snownee.fruits.compat.jade;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import snownee.fruits.duck.FFBeehiveBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.CompoundElement;

public class BeehiveProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	@Override
	public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
		if (accessor.getPickedResult().isEmpty() || !accessor.getServerData().contains("FFWaxed") || !config.get(Identifiers.MC_WAXED)) {
			return currentIcon;
		}
		IElementHelper helper = IElementHelper.get();
		IElement largeIcon = helper.item(accessor.getPickedResult());
		return new CompoundElement(largeIcon, helper.item(Items.HONEYCOMB.getDefaultInstance(), 0.5f));
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.WAXED;
	}

	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
		if (accessor.getBlockEntity() instanceof FFBeehiveBlockEntity be && be.fruits$isWaxed()) {
			compoundTag.putBoolean("FFWaxed", true);
		}
	}
}
