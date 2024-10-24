package snownee.fruits.bee.network;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket("haunting_action")
public class CHauntingActionPacket extends PacketHandler {
	public static CHauntingActionPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		return executor.apply(() -> {
			if (!canDoAction(Objects.requireNonNull(player))) {
				return;
			}
			HauntingManager manager = FFPlayer.of(player).fruits$hauntingManager();
			if (manager == null || !(FFPlayer.of(player).fruits$hauntingTarget() instanceof LivingEntity target)) {
				return;
			}
			boolean success = false;
			if (manager.hasTrait(Trait.FASTER)) {
				buffTargetAndVehicle(target, new MobEffectInstance(MobEffects.SLOW_FALLING, 20));
				buffTargetAndVehicle(target, new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 1));
				success = true;
			} else if (manager.hasTrait(Trait.FAST)) {
				buffTargetAndVehicle(target, new MobEffectInstance(MobEffects.SLOW_FALLING, 20));
				success = true;
			}
			if (manager.hasTrait(Trait.LAZY)) {
				MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 3);
				buffTargetAndVehicle(target, effectInstance);
				player.addEffect(new MobEffectInstance(effectInstance));
				success = true;
			}
			if (manager.hasTrait(Trait.PINK)) {
				Vec3 start = target.getEyePosition();
				Vec3 end = start.add(Hooks.calculateViewVector(target, player, 1).scale(8));
//				Snowball snowball = new Snowball(target.level(), target);
//				snowball.setDeltaMovement(Hooks.calculateViewVector(target, player, 1).scale(1));
//				snowball.setPos(start.x, start.y, start.z);
//				target.level().addFreshEntity(snowball);
				List<LivingEntity> entities = target.level().getEntitiesOfClass(
						LivingEntity.class,
						new AABB(start, end),
						$ -> $ != player && $ != target && $.isAlive() && !$.isSpectator());
				double distance = Double.MAX_VALUE;
				LivingEntity closest = null;
				for (LivingEntity entity : entities) {
					AABB box = entity.getBoundingBox();
					if (box.contains(start)) {
						closest = entity;
						break;
					}
					Optional<Vec3> clip = box.clip(start, end);
					if (clip.isPresent()) {
						double d = start.distanceToSqr(clip.get());
						if (d < distance) {
							distance = d;
							closest = entity;
						}
					}
				}
				if (closest != null) {
					IntList affectedEntities = new IntArrayList(2);
					if (target instanceof Mob mob) {
						mob.setAggressive(true);
						mob.setTarget(closest);
						affectedEntities.add(mob.getId());
					}
					if (closest instanceof Mob mob) {
						mob.setAggressive(true);
						mob.setTarget(target);
						affectedEntities.add(mob.getId());
					}
					if (!affectedEntities.isEmpty()) {
						success = true;
						manager.performPinkSkill();
						SSetPinkGlowPacket.send(player, affectedEntities);
					}
				}
			}

			if (success && FFCommonConfig.hauntingInitiativeSkillCooldownTicks > 0) {
				player.addEffect(new MobEffectInstance(
						MobEffects.WEAKNESS,
						FFCommonConfig.hauntingInitiativeSkillCooldownTicks,
						0,
						false,
						false,
						true));
			}
		});
	}

	public static boolean canDoAction(Player player) {
		return FFCommonConfig.hauntingInitiativeSkill && BeeModule.isHauntingNormalEntity(player, null) &&
				(FFCommonConfig.hauntingInitiativeSkillCooldownTicks <= 0 || !player.hasEffect(MobEffects.WEAKNESS));
	}

	public static void buffTargetAndVehicle(LivingEntity target, MobEffectInstance effect) {
		target.addEffect(effect);
		if (target.getRootVehicle() instanceof LivingEntity vehicle) {
			vehicle.addEffect(new MobEffectInstance(effect));
		}
	}
}
