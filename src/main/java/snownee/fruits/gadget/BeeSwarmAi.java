package snownee.fruits.gadget;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

public class BeeSwarmAi {
	public static Brain<?> makeBrain(Brain<BeeSwarm> brain) {
		initCoreActivity(brain);
		initFightActivity(brain);
		return brain;
	}

	private static void initCoreActivity(Brain<BeeSwarm> brain) {
		brain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8f), new MoveToTargetSink()));
	}

	private static void initFightActivity(Brain<BeeSwarm> brain) {
		brain.addActivityAndRemoveMemoryWhenStopped(
				Activity.FIGHT,
				10,
				ImmutableList.of(
						SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0f),
						MeleeAttack.create(15),
						StopAttackingIfTargetInvalid.create()),
				MemoryModuleType.ATTACK_TARGET);
	}

	public static void updateActivity(BeeSwarm entity) {
		entity.getBrain().setActiveActivityToFirstValid(List.of(Activity.FIGHT));
	}
}
