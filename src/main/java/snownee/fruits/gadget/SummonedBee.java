package snownee.fruits.gadget;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Dynamic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SummonedBee extends Bee implements TraceableEntity {
	protected static final List<SensorType<? extends Sensor<? super SummonedBee>>> SENSOR_TYPES = List.of(SensorType.NEAREST_LIVING_ENTITIES);
	protected static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
			MemoryModuleType.PATH,
			MemoryModuleType.LOOK_TARGET,
			MemoryModuleType.ATTACK_TARGET,
			MemoryModuleType.ATTACK_COOLING_DOWN,
			MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 5f)
				.add(Attributes.FLYING_SPEED, 0.6f)
				.add(Attributes.MOVEMENT_SPEED, 1f)
				.add(Attributes.ATTACK_DAMAGE, 2f);
	}

	@Nullable
	private LivingEntity owner;
	@Nullable
	private UUID ownerUUID;
	private int remainingAttacks = 3;
	private int cantFindTargetTicks;

	public SummonedBee(EntityType<? extends SummonedBee> entityType, Level level) {
		super(entityType, level);
		setNoGravity(true);
	}

	@Override
	protected Brain.Provider<SummonedBee> brainProvider() {
		return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
	}

	@Override
	protected Brain<?> makeBrain(Dynamic<?> dynamic) {
		return SummonedBeeAi.makeBrain(brainProvider().makeBrain(dynamic));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Brain<SummonedBee> getBrain() {
		return (Brain<SummonedBee>) super.getBrain();
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation navigation = new FlyingPathNavigation(this, level) {
			@Override
			public boolean isStableDestination(BlockPos pos) {
				return true;
			}
		};
		navigation.setCanOpenDoors(false);
		navigation.setCanFloat(true);
		navigation.setCanPassDoors(true);
		return navigation;
	}

	@Override
	protected void customServerAiStep() {
		Level level = level();
		if (getAttackTarget() == null) {
			++cantFindTargetTicks;
		} else {
			cantFindTargetTicks = 0;
		}
		if (tickCount >= 200 || cantFindTargetTicks >= 15 || remainingAttacks <= 0 && --remainingAttacks < -10) {
			level.broadcastEntityEvent(this, (byte) 60); // spawn poof particles
			discard();
			return;
		}
		if (tickCount == 1) {
			level.broadcastEntityEvent(this, (byte) 60); // spawn poof particles
		}
		level.getProfiler().push("beeBrain");
		getBrain().tick((ServerLevel) level, this);
		level.getProfiler().pop();
		level.getProfiler().push("beeActivityUpdate");
		SummonedBeeAi.updateActivity(this);
		level.getProfiler().pop();
		super.customServerAiStep();
	}

	@Override
	public boolean canBeLeashed(Player player) {
		return false;
	}

	@Override
	protected boolean canRide(Entity vehicle) {
		return false;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.removeAllGoals($ -> true);
		targetSelector.removeAllGoals($ -> true);
	}

	@Override
	public boolean isAngry() {
		return true;
	}

	@Override
	public boolean isBaby() {
		return true;
	}

	@Override
	public @Nullable Bee getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}

	@Override
	public boolean shouldDropExperience() {
		return false;
	}

	@Override
	protected void sendDebugPackets() {
		super.sendDebugPackets();
		DebugPackets.sendEntityBrain(this);
	}

	protected Optional<? extends LivingEntity> findNearestValidAttackTarget() {
		return this.getBrain()
				.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
				.orElse(NearestVisibleLivingEntities.empty())
				.findClosest(this::isTargetable);
	}

	public boolean isTargetable(LivingEntity entity) {
		if (this.remainingAttacks <= 0) {
			return false;
		}
		Entity owner = getOwner();
		if (entity instanceof TraceableEntity traceable && traceable.getOwner() == owner) {
			return false;
		}
		if (owner == entity || owner != null && owner.isAlliedTo(entity)) {
			return false;
		}
		return entity.canBeSeenAsEnemy() && !GadgetModule.SUMMONED_BEE.is(entity.getType());
	}

	public void setAttackTarget(LivingEntity target) {
		if (isTargetable(target)) {
			this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
		}
	}

	public @Nullable LivingEntity getAttackTarget() {
		return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	@Override
	public @Nullable Entity getOwner() {
		Entity entity;
		if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel level &&
				(entity = level.getEntity(this.ownerUUID)) instanceof LivingEntity) {
			this.owner = (LivingEntity) entity;
		}
		return this.owner;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUUID = owner == null ? null : owner.getUUID();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (ownerUUID != null) {
			compound.putUUID("owner", ownerUUID);
		}
		compound.putInt("remaining_attacks", remainingAttacks);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		if (compound.hasUUID("owner")) {
			ownerUUID = compound.getUUID("owner");
		}
		if (compound.contains("remaining_attacks")) {
			remainingAttacks = compound.getInt("remaining_attacks");
		}
	}

	@Override
	public void stopBeingAngry() {
		// NO-OP
	}

	@Override
	public boolean hasStung() {
		return false;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		this.remainingAttacks--;
		return super.doHurtTarget(target);
	}
}
