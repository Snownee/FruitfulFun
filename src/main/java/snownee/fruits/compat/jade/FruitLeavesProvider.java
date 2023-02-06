package snownee.fruits.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.fruits.FruitType;
import snownee.fruits.FruitsMod;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FruitLeavesProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

	public static final ResourceLocation UID = new ResourceLocation(FruitsMod.ID, "fruit_leaves");
	public static final FruitLeavesProvider INSTANCE = new FruitLeavesProvider();

	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		CompoundTag data = blockAccessor.getServerData();
		if (!data.contains("type")) {
			return;
		}
		iTooltip.add(Component.literal("type: " + data.getString("type")));
		iTooltip.add(Component.literal("death: " + data.getInt("death")));
	}

	@Override
	public void appendServerData(CompoundTag data, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
		if (blockEntity instanceof FruitTreeBlockEntity tree) {
			data.putString("type", FruitType.REGISTRY.getKey(tree.type).toString());
			data.putInt("death", tree.getRawDeathRate());
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
