package snownee.fruits.cherry.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import snownee.fruits.CoreModule;
import snownee.kiwi.util.NBTHelper;

public class SlidingDoorEntity extends Entity {

	public BlockPos doorPos = BlockPos.ZERO;

	public SlidingDoorEntity(EntityType<?> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
	}

	public SlidingDoorEntity(World worldIn, BlockPos doorPos) {
		super(CoreModule.SLIDING_DOOR, worldIn);
		setPosition(doorPos.getX() + 0.5, doorPos.getY(), doorPos.getZ() + 0.5);
		this.doorPos = doorPos;
		if (world.isBlockPresent(doorPos))
			update(worldIn.getBlockState(doorPos));
	}

	@Override
	protected void registerData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		doorPos = NBTHelper.of(compound).getPos("DoorPos");
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		NBTHelper.of(compound).setPos("DoorPos", doorPos);
	}

	@Override
	public void tick() {
		if (firstUpdate && !world.isRemote && getBoundingBox().getYSize() == 0) {
			if (world.isBlockPresent(doorPos))
				update(world.getBlockState(doorPos));
		}
		firstUpdate = false;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void update(BlockState state) {
		if (!(state.getBlock() instanceof SlidingDoorBlock))
			return;
		setBoundingBox(extendBoundingBox(state.getShape(world, doorPos).withOffset(doorPos.getX(), doorPos.getY(), doorPos.getZ())));
		//resetPositionToBB();
	}

	public AxisAlignedBB extendBoundingBox(VoxelShape shape) {
		return new AxisAlignedBB(shape.getStart(Direction.Axis.X), shape.getStart(Direction.Axis.Y), shape.getStart(Direction.Axis.Z), shape.getEnd(Direction.Axis.X), shape.getEnd(Direction.Axis.Y) + 1, shape.getEnd(Direction.Axis.Z));
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		//NOOP
	}

	@Override
	public void recalculateSize() {
		//NOOP
	}

	@Override
	public void setPosition(double x, double y, double z) {
		setRawPosition(x, y, z);
	}

	@Override
	public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
		return ActionResultType.PASS;
	}

	@Override
	public boolean hitByEntity(Entity entityIn) {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return isAlive();
	}

	@Override
	public boolean canChangeDimension() {
		return false;
	}

	@Override
	public void onKillCommand() {
		//NOOP
	}
}
