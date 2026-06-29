package snownee.fruits.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FruitLeavesDebugProvider implements IServerDataProvider<BlockAccessor> {
	public static final Identifier UID = FruitfulFun.id("fruit_leaves");

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		if (!(accessor.getBlockEntity() instanceof FruitTreeBlockEntity tree)) {
			return;
		}
		data.putString("Type", tree.type.getRegisteredName());
		data.putInt("Lifespan", tree.getLifespan());
		data.putInt("MaxLifespan", tree.getMaxLifespan());
		data.putInt("Produced", tree.getFruitProduced());
	}

	@Override
	public Identifier getUid() {
		return UID;
	}

	public static class Client extends FruitLeavesDebugProvider implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			CompoundTag data = accessor.getServerData();
			if (!data.contains("Type")) {
				return;
			}
			tooltip.add(Component.literal("%s: %s produced".formatted(
					data.getString("Type").orElseThrow(),
					data.getInt("Produced").orElseThrow())));
			tooltip.add(Component.literal("Lifespan: %s/%s".formatted(
					data.getInt("Lifespan").orElseThrow(),
					data.getInt("MaxLifespan").orElseThrow())));
		}

		@Override
		public boolean isRequired() {
			return true;
		}
	}
}
