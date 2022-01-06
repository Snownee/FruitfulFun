package snownee.fruits;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEventListener;
import snownee.fruits.FruitsConfig.DropMode;
import snownee.fruits.block.entity.FruitTreeBlockEntity;

public class FruitDropGameEvent extends CancellableGameEvent {

	public Supplier<ItemEntity> runnable;

	public FruitDropGameEvent(String id, int radius) {
		super(id, radius);
	}

	@Override
	public GameEventListener post(LevelAccessor level, BlockPos pos, @Nullable Entity entity) {
		GameEventListener listener = super.post(level, pos, entity);
		if (runnable != null) {
			ItemEntity itemEntity = runnable.get();
			runnable = null;
			if (listener instanceof FruitTreeBlockEntity) {
				DropMode mode = FruitsConfig.getDropMode(level);
				if (mode == DropMode.ONE_BY_ONE) {
					((FruitTreeBlockEntity) listener).setOnlyItem(itemEntity);
				}
			}
		}
		return listener;
	}

}
