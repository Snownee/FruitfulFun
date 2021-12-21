package snownee.fruits;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;

public class CancellableGameEvent extends GameEvent {

	private GameEventListener receiver;

	public CancellableGameEvent(String id, int radius) {
		super(id, radius);
	}

	public GameEventListener post(LevelAccessor level, BlockPos pos, @Nullable Entity entity) {
		this.receiver = null;
		level.gameEvent(entity, this, pos);
		GameEventListener receiver = this.receiver;
		this.receiver = null;
		return receiver;
	}

	public void swallow(GameEventListener listener) {
		if (!isActive()) {
			throw new IllegalStateException("Game event handled twice: " + listener);
		}
		receiver = listener;
	}

	public boolean isActive() {
		return receiver == null;
	}

}
