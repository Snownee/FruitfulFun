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

public class FruitLeavesProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

	public static final ResourceLocation UID = new ResourceLocation(FruitfulFun.ID, "fruit_leaves");
	public static final FruitLeavesProvider INSTANCE = new FruitLeavesProvider();

	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		CompoundTag data = blockAccessor.getServerData();
		if (!data.contains("Type")) {
			return;
		}
		iTooltip.add(Component.literal("type: " + data.getString("Type")));
		iTooltip.add(Component.literal("death: " + data.getInt("Death")));
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		if (accessor.getBlockEntity() instanceof FruitTreeBlockEntity tree) {
			data.putString("Type", FFRegistries.FRUIT_TYPE.getKey(tree.type).toString());
			data.putInt("Death", tree.getRawDeathRate());
		}
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
