package snownee.fruits.block.entity;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.CoreModule;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.kiwi.loader.Platform;

public class SlidingDoorEntity extends Entity {
	public static final float OPEN_STEP = 1 / 20F;
	public static final float CLOSE_STEP = 1 / 24F;
	private static final EntityDataAccessor<Optional<BlockPos>> DATA_ID_DOOR_POS = SynchedEntityData.defineId(
			SlidingDoorEntity.class,
			EntityDataSerializers.OPTIONAL_BLOCK_POS);
	private static final EntityDataAccessor<Boolean> DATA_ID_OPEN = SynchedEntityData.defineId(
			SlidingDoorEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> DATA_ID_SAVED_PROGRESS = SynchedEntityData.defineId(
			SlidingDoorEntity.class,
			EntityDataSerializers.FLOAT);
	public BlockState topState = Blocks.AIR.defaultBlockState();
	public BlockState bottomState = Blocks.AIR.defaultBlockState();
	public Vec3 initialPos = Vec3.ZERO;
	public Vec3 openPos = Vec3.ZERO;
	public float progress;
	private int discardTimer;
	private boolean savedProgressLoaded;

	public SlidingDoorEntity(EntityType<?> entityTypeIn, Level level) {
		super(entityTypeIn, level);
		setNoGravity(true);
	}

	public SlidingDoorEntity(Level level) {
		this(CoreModule.SLIDING_DOOR.get(), level);
	}

	@Override
	public void tick() {
		if (!(bottomState.getBlock() instanceof SlidingDoorBlock)) {
			discard();
			return;
		}
		Vec3 oldOffset = getLerpedOffset(0.5F);
		if (isOpen() && progress < 1) {
			progress = Math.min(progress + OPEN_STEP, 1);
			updatePos(oldOffset);
		} else if (!isOpen() && progress > 0) {
			progress = Math.max(progress - CLOSE_STEP, 0);
			updatePos(oldOffset);
		} else {
			updatePos(null);
		}
		if (progress == 0 && !level().isClientSide()) {
			BlockPos pos = doorPos();
			if (pos != null && discardTimer == 0) {
				level().setBlockAndUpdate(
						pos,
						bottomState.setValue(DoorBlock.POWERED, SlidingDoorBlock.isPowered(bottomState, level(), pos)));
			}
			if (++discardTimer == 3) {
				discard();
			}
		}
	}

	public void updatePos(@Nullable Vec3 oldOffset) {
		BlockPos pos = doorPos();
		if (pos == null) {
			return;
		}
		Vec3 offset = getLerpedOffset(0.5F);
		if (oldOffset != null) {
			Vec3 delta = offset.subtract(oldOffset);
			move(MoverType.SELF, delta);
			AABB deltaBox = Shulker.getProgressDeltaAabb(
					1,
					Direction.getApproximateNearest(delta),
					0,
					(float) delta.horizontalDistance(),
					position()).expandTowards(0, 1, 0);
			for (Entity entity : level().getPushableEntities(this, deltaBox)) {
				entity.move(MoverType.SHULKER, delta);
			}
		} else {
			setPos(initialPos.x + offset.x, initialPos.y + offset.y, initialPos.z + offset.z);
		}
	}

	@Override
	protected AABB makeBoundingBox(Vec3 position) {
		//noinspection ConstantValue
		if (bottomState == null || !(bottomState.getBlock() instanceof SlidingDoorBlock)) {
			return super.makeBoundingBox(position);
		}
		VoxelShape shape = SlidingDoorBlock.getActualShape(bottomState);
		return AABB.ofSize(
				position.add(0, 1, 0),
				shape.max(Direction.Axis.X) - shape.min(Direction.Axis.X),
				2,
				shape.max(Direction.Axis.Z) - shape.min(Direction.Axis.Z));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
		if (level().isClientSide() && !savedProgressLoaded && accessor.equals(DATA_ID_SAVED_PROGRESS)) {
			progress = entityData.get(DATA_ID_SAVED_PROGRESS);
			savedProgressLoaded = true;
			updatePos(null);
		}
		if (!accessor.equals(DATA_ID_DOOR_POS)) {
			return;
		}
		BlockPos pos = doorPos();
		if (pos == null) {
			return;
		}
		BlockState blockState = level().getBlockState(pos);
		if (blockState.getBlock() instanceof SlidingDoorBlock) {
			setBottomState(blockState);
			VoxelShape shape = SlidingDoorBlock.getActualShape(bottomState);
			initialPos = shape.bounds().getBottomCenter().add(pos.getX(), pos.getY(), pos.getZ());
			updatePos(null);
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder entityData) {
		entityData.define(DATA_ID_DOOR_POS, Optional.empty());
		entityData.define(DATA_ID_SAVED_PROGRESS, 0F);
		entityData.define(DATA_ID_OPEN, false);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		input.read("progress", Codec.FLOAT).ifPresent(i -> progress = i);
		entityData.set(DATA_ID_SAVED_PROGRESS, progress);
		input.read("open", Codec.BOOL).ifPresent(this::setOpen);
		input.read("doorPos", BlockPos.CODEC).ifPresent(this::setDoorPos);
		input.read("bottomState", BlockState.CODEC).ifPresent(this::setBottomState);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		output.storeNullable("doorPos", BlockPos.CODEC, doorPos());
		output.store("open", Codec.BOOL, isOpen());
		output.store("progress", Codec.FLOAT, progress);
		output.store("bottomState", BlockState.CODEC, bottomState);
	}

	public void setBottomState(BlockState state) {
		bottomState = state.setValue(SlidingDoorBlock.OPEN, false);
		topState = bottomState.setValue(SlidingDoorBlock.HALF, DoubleBlockHalf.UPPER);
		AABB closedShape = SlidingDoorBlock.getActualShape(bottomState).bounds();
		AABB openShape = SlidingDoorBlock.getActualShape(bottomState.setValue(SlidingDoorBlock.OPEN, true)).bounds();
		openPos = openShape.getMinPosition().subtract(closedShape.getMinPosition());
	}

	public void setDoorPos(BlockPos pos) {
		entityData.set(DATA_ID_DOOR_POS, Optional.of(pos));
	}

	@Nullable
	public BlockPos doorPos() {
		return entityData.get(DATA_ID_DOOR_POS).orElse(null);
	}

	public void setOpen(boolean open) {
		entityData.set(DATA_ID_OPEN, open);
		if (level().isClientSide() && progress != 0 && progress != 1) {
			float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
			progress = getOpenProgress(partialTick);
			if (open) {
				progress -= OPEN_STEP * partialTick;
			} else {
				progress += CLOSE_STEP * partialTick;
			}
			progress = Mth.clamp(progress, 0, 1);
			updatePos(null);
			SoundManager soundManager = Minecraft.getInstance().getSoundManager();
			soundManager.stop(CoreModule.OPEN_SOUND.get().location(), SoundSource.BLOCKS);
			soundManager.stop(CoreModule.CLOSE_SOUND.get().location(), SoundSource.BLOCKS);
		}
	}

	public boolean isOpen() {
		return entityData.get(DATA_ID_OPEN);
	}

	public float getOpenProgress(float partialTicks) {
		if (isOpen()) {
			return Math.min(progress + OPEN_STEP * partialTicks, 1);
		} else {
			return Math.max(progress - CLOSE_STEP * partialTicks, 0);
		}
	}

	public Vec3 getLerpedOffset(float partialTicks) {
		float progress = getOpenProgress(partialTicks);
//		progress = isOpen() ? Ease.outCubic(progress) : Ease.inCubic(progress);
		progress = Ease.inOutCubic(progress);
		return openPos.scale(progress);
	}

	@Override
	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	@Override
	public boolean canBeCollidedWith(@Nullable Entity other) {
		return isAlive();
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public void push(Entity entity) {
		//NOOP
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
	public @Nullable Entity teleport(TeleportTransition transition) {
		return null;
	}

//	@Override
//	public void kill(ServerLevel level) {
//		//NOOP
//	}
}
