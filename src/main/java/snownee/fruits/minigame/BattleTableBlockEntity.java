package snownee.fruits.minigame;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BattleTableBlockEntity extends BlockEntity {
	private @Nullable UUID left;
	private @Nullable UUID right;
	private final Set<UUID> spectators = new LinkedHashSet<>();
	private boolean active;

	public BattleTableBlockEntity(BlockPos pos, BlockState state) {
		super(MinigameModule.BATTLE_TABLE_ENTITY.get(), pos, state);
	}

	public @Nullable UUID left() {
		return left;
	}

	public @Nullable UUID right() {
		return right;
	}

	public Set<UUID> spectators() {
		return spectators;
	}

	public boolean active() {
		return active;
	}

	void setLeft(@Nullable UUID player) {
		left = player;
	}

	void setRight(@Nullable UUID player) {
		right = player;
	}

	void setActive(boolean active) {
		this.active = active;
	}

	void clearWaiting() {
		left = null;
		right = null;
		spectators.clear();
	}

	@Override
	public void setRemoved() {
		MinigameManager.removeTable(this);
		super.setRemoved();
	}
}
