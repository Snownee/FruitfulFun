package snownee.fruits.gadget;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
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
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SummonedBee extends Bee implements TraceableEntity {
	protected static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
			MemoryModuleType.PATH,
			MemoryModuleType.LOOK_TARGET,
			MemoryModuleType.ATTACK_TARGET,
			MemoryModuleType.ATTACK_COOLING_DOWN,
			MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

	protected static final Brain.Provider<SummonedBee> BRAIN_PROVIDER = Brain.provider(
			List.of(SensorType.NEAREST_LIVING_ENTITIES),
			_ -> SummonedBeeAi.getActivities());

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 5f).add(Attributes.FLYING_SPEED, 0.6f).add(
				Attributes.MOVEMENT_SPEED,
				1f).add(Attributes.ATTACK_DAMAGE, 2f);
	}

	@Nullable
	private EntityReference<LivingEntity> owner;
	private int remainingAttacks = 3;
	private int lostTargetTicks;

	public SummonedBee(EntityType<? extends SummonedBee> entityType, Level level) {
		super(entityType, level);
		setNoGravity(true);
	}

	@Override
	protected Brain<? extends LivingEntity> makeBrain(Brain.Packed packedBrain) {
		return BRAIN_PROVIDER.makeBrain(this, packedBrain);
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
		navigation.setRequiredPathLength(48.0F);
		return navigation;
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		if (getAttackTarget() == null) {
			++lostTargetTicks;
		} else {
			lostTargetTicks = 0;
		}
		if (tickCount >= 200 || lostTargetTicks >= 15 || remainingAttacks <= 0 && --remainingAttacks < -10) {
			level.broadcastEntityEvent(this, (byte) 60); // spawn poof particles
			discard();
			return;
		}
		if (tickCount == 1) {
			level.broadcastEntityEvent(this, (byte) 60); // spawn poof particles
		}
		ProfilerFiller profiler = Profiler.get();
		profiler.push("beeBrain");
		getBrain().tick(level, this);
		profiler.pop();
		profiler.push("beeActivityUpdate");
		SummonedBeeAi.updateActivity(this);
		profiler.pop();
		super.customServerAiStep(level);
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}

	@Override
	protected boolean canRide(Entity vehicle) {
		return false;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.removeAllGoals(_ -> true);
		targetSelector.removeAllGoals(_ -> true);
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

	protected static Optional<? extends LivingEntity> findNearestValidAttackTarget(ServerLevel level, SummonedBee bee) {
		return bee.getBrain()
				.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
				.orElse(NearestVisibleLivingEntities.empty())
				.findClosest(target -> bee.isTargetable(target) && Sensor.isEntityAttackable(level, bee, target));
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
		return EntityReference.getLivingEntity(owner, level());
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = EntityReference.of(owner);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		EntityReference.store(owner, output, "owner");
		output.putInt("remaining_attacks", remainingAttacks);
	}

	@Override
	public void load(ValueInput input) {
		super.load(input);
		owner = EntityReference.read(input, "owner");
		remainingAttacks = input.getIntOr("remaining_attacks", 1);
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
	protected void actuallyHurt(ServerLevel level, DamageSource source, float dmg) {
		super.actuallyHurt(level, source, dmg);
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		if (super.doHurtTarget(level, target)) {
			this.remainingAttacks--;
			return true;
		}
		return false;
	}
}
