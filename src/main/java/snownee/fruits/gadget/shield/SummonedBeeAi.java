package snownee.fruits.gadget.shield;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

public class SummonedBeeAi {
	private static ActivityData<SummonedBee> initCoreActivity() {
		return ActivityData.create(
				Activity.CORE,
				0,
				ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink()));
	}

	private static ActivityData<SummonedBee> initIdleActivity() {
		return ActivityData.create(
				Activity.IDLE,
				10,
				ImmutableList.of(StartAttacking.create(SummonedBee::findNearestValidAttackTarget)));
	}

	private static ActivityData<SummonedBee> initFightActivity() {
		return ActivityData.create(
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

	public static List<ActivityData<SummonedBee>> getActivities() {
		return List.of(initCoreActivity(), initIdleActivity(), initFightActivity());
	}
}
