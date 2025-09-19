package snownee.fruits.gadget;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

public class SummonedBeeAi {
	public static Brain<?> makeBrain(Brain<SummonedBee> brain) {
		initCoreActivity(brain);
		initIdleActivity(brain);
		initFightActivity(brain);
		brain.setCoreActivities(Set.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.useDefaultActivity();
		return brain;
	}

	private static void initCoreActivity(Brain<SummonedBee> brain) {
		brain.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink()));
	}

	private static void initIdleActivity(Brain<SummonedBee> brain) {
		brain.addActivity(
				Activity.IDLE,
				10,
				ImmutableList.of(StartAttacking.create(SummonedBee::findNearestValidAttackTarget)));
	}

	private static void initFightActivity(Brain<SummonedBee> brain) {
		brain.addActivityAndRemoveMemoryWhenStopped(
				Activity.FIGHT,
				10,
				ImmutableList.of(
						EyeBasedSetWalkTargetFromAttackTarget.create(1.5f),
						MeleeAttack.create(15),
						StopAttackingIfTargetInvalid.create()),
				MemoryModuleType.ATTACK_TARGET);
	}

	public static void updateActivity(SummonedBee bee) {
		bee.getBrain().setActiveActivityToFirstValid(List.of(Activity.FIGHT, Activity.IDLE));
	}
}
