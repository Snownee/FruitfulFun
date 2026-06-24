package snownee.fruits.block.entity;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.CoreModule;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.kiwi.loader.Platform;

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
	public void tick() {
		if (getBoundingBox().getYsize() < 2 || tickCount % 20 == 1) {
			setPos(getX(), getY(), getZ());
			if (getBoundingBox() == INITIAL_AABB) {
				discard();
			}
		}
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
		return Platform.defaultAddEntityPacket(this, serverEntity);
	}

	@Override
	protected AABB makeBoundingBox(Vec3 position) {
		BlockPos pos = blockPosition();
		BlockState state = level().getBlockState(pos);
		if (!(state.getBlock() instanceof SlidingDoorBlock)) {
			return INITIAL_AABB;
		}
		VoxelShape shape = state.getShape(level(), pos).move(pos.getX(), pos.getY(), pos.getZ());
		return new AABB(
				shape.min(Direction.Axis.X), shape.min(Direction.Axis.Y), shape.min(Direction.Axis.Z), shape.max(Direction.Axis.X),
				shape.max(Direction.Axis.Y) + 1, shape.max(Direction.Axis.Z));
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
	public boolean canBeCollidedWith(@Nullable Entity other) {
		return isAlive();
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}


	@Override
	protected void defineSynchedData(SynchedEntityData.Builder entityData) {
		//NOOP
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		//NOOP
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
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
