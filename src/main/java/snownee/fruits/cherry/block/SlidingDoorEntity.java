package snownee.fruits.cherry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import snownee.fruits.CoreModule;
import snownee.kiwi.util.NBTHelper;

public class SlidingDoorEntity extends Entity {

	public BlockPos doorPos = BlockPos.ZERO;

	public SlidingDoorEntity(EntityType<?> entityTypeIn, Level level) {
		super(entityTypeIn, level);
		noPhysics = true;
	}

	public SlidingDoorEntity(Level level, BlockPos doorPos) {
		this(CoreModule.SLIDING_DOOR, level);
		setPos(doorPos.getX() + 0.5, doorPos.getY(), doorPos.getZ() + 0.5);
		this.doorPos = doorPos;
		if (level.isLoaded(doorPos))
			update(level.getBlockState(doorPos));
	}

	@Override
	protected void defineSynchedData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		doorPos = NBTHelper.of(compound).getPos("DoorPos");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		NBTHelper.of(compound).setPos("DoorPos", doorPos);
	}

	@Override
	public void tick() {
		if (firstTick && !level.isClientSide && getBoundingBox().getYsize() == 0) {
			if (level.isLoaded(doorPos))
				update(level.getBlockState(doorPos));
		}
		firstTick = false;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void update(BlockState state) {
		if (!(state.getBlock() instanceof SlidingDoorBlock))
			return;
		setBoundingBox(extendBoundingBox(state.getShape(level, doorPos).move(doorPos.getX(), doorPos.getY(), doorPos.getZ())));
		//resetPositionToBB();
	}

	public AABB extendBoundingBox(VoxelShape shape) {
		return new AABB(shape.min(Direction.Axis.X), shape.min(Direction.Axis.Y), shape.min(Direction.Axis.Z), shape.max(Direction.Axis.X), shape.max(Direction.Axis.Y) + 1, shape.max(Direction.Axis.Z));
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
	public void setPos(double x, double y, double z) {
		setPosRaw(x, y, z);
	}

	public boolean hitByEntity(Entity entityIn) {
		return true;
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

}
