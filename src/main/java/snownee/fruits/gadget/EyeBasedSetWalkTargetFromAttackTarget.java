package snownee.fruits.gadget;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class EyeBasedSetWalkTargetFromAttackTarget {
	public static BehaviorControl<Mob> create(float speedModifier) {
		return EyeBasedSetWalkTargetFromAttackTarget.create(livingEntity -> speedModifier);
	}

	public static BehaviorControl<Mob> create(Function<LivingEntity, Float> speedModifier) {
		return BehaviorBuilder.create((BehaviorBuilder.Instance<Mob> instance) -> instance.group(
				instance.registered(MemoryModuleType.WALK_TARGET),
				instance.registered(MemoryModuleType.LOOK_TARGET),
				instance.present(MemoryModuleType.ATTACK_TARGET),
				instance.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(
				instance, (walkTarget, lookTarget, attackTarget, entities) -> (level, mob, l) -> {
					LivingEntity target = instance.get(attackTarget);
					Optional<NearestVisibleLivingEntities> optional = instance.tryGet(entities);
					if (optional.isPresent() && optional.get().contains(target) && BehaviorUtils.isWithinAttackRange(mob, target, 1)) {
						walkTarget.erase();
					} else {
						EyeBasedEntityTracker entityTracker = new EyeBasedEntityTracker(target);
						lookTarget.set(entityTracker);
						walkTarget.set(new WalkTarget(entityTracker, speedModifier.apply(mob), 0));
					}
					return true;
				}));
	}
}
