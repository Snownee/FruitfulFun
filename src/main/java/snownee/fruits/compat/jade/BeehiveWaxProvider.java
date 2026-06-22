package snownee.fruits.compat.jade;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import snownee.fruits.duck.FFBeehiveBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.impl.ui.CompoundElement;

public class BeehiveWaxProvider implements StreamServerDataProvider<BlockAccessor, Boolean> {
	@Override
	public Identifier getUid() {
		return JadeCompat.WAXED;
	}

	@Override
	public @Nullable Boolean streamData(BlockAccessor blockAccessor) {
		if (blockAccessor.getBlockEntity() instanceof FFBeehiveBlockEntity be && be.fruits$isWaxed()) {
			return true;
		}
		return null;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, Boolean> streamCodec() {
		return ByteBufCodecs.BOOL.cast();
	}

	public static class Client extends BeehiveWaxProvider implements IBlockComponentProvider {
		@Override
		public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, @Nullable Element currentIcon) {
			if (accessor.getPickedResult().isEmpty() || !config.get(JadeIds.MC_WAXED)) {
				return currentIcon;
			}
			Element largeIcon = JadeUI.item(accessor.getPickedResult());
			return new CompoundElement(largeIcon, JadeUI.item(Items.HONEYCOMB.getDefaultInstance(), 0.5f));
		}

		@Override
		public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
			// NO-OP
		}

		@Override
		public boolean isRequired() {
			return true;
		}
	}
}
