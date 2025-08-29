package snownee.fruits.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FruitLeavesDebugProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

	public static final ResourceLocation UID = FruitfulFun.id("fruit_leaves");

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		CompoundTag data = accessor.getServerData();
		if (!data.contains("Type")) {
			return;
		}
		tooltip.add(Component.literal("%s: %s produced".formatted(data.getString("Type"), data.getInt("Produced"))));
		tooltip.add(Component.literal("Lifespan: %s/%s".formatted(data.getInt("Lifespan"), data.getInt("MaxLifespan"))));
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		FruitTreeBlockEntity tree = (FruitTreeBlockEntity) accessor.getBlockEntity();
		data.putString("Type", FFRegistries.FRUIT_TYPE.getKey(tree.type).toString());
		data.putInt("Lifespan", tree.getLifespan());
		data.putInt("MaxLifespan", tree.getMaxLifespan());
		data.putInt("Produced", tree.getFruitProduced());
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
