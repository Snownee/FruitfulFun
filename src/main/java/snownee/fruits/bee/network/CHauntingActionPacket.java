package snownee.fruits.bee.network;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
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
			HauntingManager manager = ((FFPlayer) player).fruits$hauntingManager();
			if (manager == null || !(((FFPlayer) player).fruits$hauntingTarget() instanceof LivingEntity target)) {
				return;
			}
			boolean success = false;
			if (manager.hasTrait(Trait.FASTER)) {
				target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20));
				target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 1));
				success = true;
			} else if (manager.hasTrait(Trait.FAST)) {
				target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20));
				success = true;
			}
			if (manager.hasTrait(Trait.LAZY)) {
				MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 3);
				target.addEffect(effectInstance);
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
					boolean _success = false;
					if (target instanceof Mob mob) {
						mob.setAggressive(true);
						mob.setTarget(closest);
						_success = true;
					}
					if (closest instanceof Mob mob) {
						mob.setAggressive(true);
						mob.setTarget(target);
						_success = true;
					}
					if (_success) {
						success = true;
						manager.performPinkSkill();
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
}
