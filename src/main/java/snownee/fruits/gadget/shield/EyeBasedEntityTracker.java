package snownee.fruits.gadget.shield;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.phys.Vec3;

public class EyeBasedEntityTracker extends EntityTracker {
	public EyeBasedEntityTracker(Entity entity) {
		super(entity, true);
	}

	@Override
	public Vec3 currentPosition() {
		return getEntity().getEyePosition();
	}

	@Override
	public BlockPos currentBlockPosition() {
		return BlockPos.containing(currentPosition());
	}
}
