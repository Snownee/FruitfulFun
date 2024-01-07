package snownee.fruits.compat.jade;

import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class InspectorBlockProvider implements IServerDataProvider<BlockAccessor> {
	private final Function<BlockAccessor, Entity> entityConvertor;

	public InspectorBlockProvider(Function<BlockAccessor, Entity> entityConvertor) {
		this.entityConvertor = entityConvertor;
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		Entity entity = entityConvertor.apply(accessor);
		if (entity instanceof Bee bee) {
			InspectorProvider.appendServerData(accessor, bee);
		}
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.INSPECTOR_BLOCK;
	}
}
