package snownee.fruits.bee.network;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;

public class InspectAction {
	public static final InspectAction BEE = new InspectAction(true);
	public static final InspectAction SCENT = new InspectAction(false);

	public final boolean recommendJade;

	public InspectAction(boolean recommendJade) {
		this.recommendJade = recommendJade;
	}

	public static @Nullable InspectAction get(@Nullable InspectTarget target, Level level) {
		if (target == null || !target.isFor(level)) {
			return null;
		}
		Entity entity = target.getEntity(level);
		if (entity instanceof Bee bee && !bee.isDeadOrDying()) {
			return BEE;
		}
		if (entity == null && target instanceof InspectTarget.BlockTarget) {
			return SCENT;
		}
		return null;
	}
}
