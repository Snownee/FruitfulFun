package snownee.fruits.gadget;

import java.util.List;

import com.mojang.serialization.Dynamic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BeeSwarm extends PathfinderMob {
	protected static final List<SensorType<? extends Sensor<? super BeeSwarm>>> SENSOR_TYPES = List.of(SensorType.NEAREST_LIVING_ENTITIES);
	protected static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
			MemoryModuleType.PATH,
			MemoryModuleType.ATTACK_TARGET,
			MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
			MemoryModuleType.LIKED_PLAYER);

	public BeeSwarm(EntityType<? extends BeeSwarm> entityType, Level level) {
		super(entityType, level);
		moveControl = new FlyingMoveControl(this, 20, true);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
		navigation.setCanOpenDoors(false);
		navigation.setCanFloat(true);
		navigation.setCanPassDoors(true);
		return navigation;
	}

	@Override
	protected Brain.Provider<BeeSwarm> brainProvider() {
		return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
	}

	@Override
	protected Brain<?> makeBrain(Dynamic<?> dynamic) {
		return BeeSwarmAi.makeBrain(brainProvider().makeBrain(dynamic));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Brain<BeeSwarm> getBrain() {
		return (Brain<BeeSwarm>) super.getBrain();
	}

	@Override
	protected void customServerAiStep() {
		Level level = level();
		level.getProfiler().push("allayBrain");
		getBrain().tick((ServerLevel) level, this);
		level.getProfiler().pop();
		level.getProfiler().push("allayActivityUpdate");
		BeeSwarmAi.updateActivity(this);
		level.getProfiler().pop();
		super.customServerAiStep();
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0, 0, 0);
		}
	}

	@Override
	public boolean canBeHitByProjectile() {
		return false;
	}

	@Override
	public boolean canBeLeashed(Player player) {
		return false;
	}

	@Override
	public boolean canFreeze() {
		return false;
	}

	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}
}
