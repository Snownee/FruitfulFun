package snownee.fruits.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.CoreModule;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.fruits.util.CommonProxy;

public class SlidingDoorEntity extends Entity {
	private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

	public SlidingDoorEntity(EntityType<?> entityTypeIn, Level level) {
		super(entityTypeIn, level);
		noPhysics = true;
	}

	public SlidingDoorEntity(Level level) {
		this(CoreModule.SLIDING_DOOR.get(), level);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	public void tick() {
		if (getBoundingBox().getYsize() < 2 || tickCount % 20 == 1) {
			setPos(getX(), getY(), getZ());
			if (getBoundingBox() == INITIAL_AABB) {
				discard();
			}
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return CommonProxy.getAddEntityPacket(this);
	}

	@Override
	protected AABB makeBoundingBox() {
		BlockPos pos = blockPosition();
		BlockState state = level().getBlockState(pos);
		if (!(state.getBlock() instanceof SlidingDoorBlock)) {
			return INITIAL_AABB;
		}
		VoxelShape shape = state.getShape(level(), pos).move(pos.getX(), pos.getY(), pos.getZ());
		AABB aabb = new AABB(
				shape.min(Direction.Axis.X),
				shape.min(Direction.Axis.Y),
				shape.min(Direction.Axis.Z),
				shape.max(Direction.Axis.X),
				shape.max(Direction.Axis.Y) + 1,
				shape.max(Direction.Axis.Z));
		return aabb;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		//NOOP
	}

	@Override
	public void refreshDimensions() {
		//NOOP
	}

	@Override
	public boolean canBeCollidedWith() {
		return isAlive();
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public void kill() {
		//NOOP
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}

}
