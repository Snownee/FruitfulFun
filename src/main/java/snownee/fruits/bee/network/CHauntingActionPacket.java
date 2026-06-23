package snownee.fruits.bee.network;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CHauntingActionPacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CHauntingActionPacket> TYPE = new CustomPacketPayload.Type<>(
			snownee.fruits.FruitfulFun.id("haunting_action"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CHauntingActionPacket> STREAM_CODEC = StreamCodec.unit(new CHauntingActionPacket());

	@Override
	public CustomPacketPayload.Type<CHauntingActionPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CHauntingActionPacket> {
		@Override
		public void handle(CHauntingActionPacket packet, PayloadContext context) {
			context.execute(() -> {
				ServerPlayer player = Objects.requireNonNull(context.serverPlayer());
				if (!canDoAction(player)) {
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
					MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.RESISTANCE, 10, 3);
					buffTargetAndVehicle(target, effectInstance);
					player.addEffect(new MobEffectInstance(effectInstance));
					success = true;
				}
				if (manager.hasTrait(Trait.PINK)) {
					Vec3 start = target.getEyePosition();
					Vec3 end = start.add(Hooks.calculateViewVector(target, player, 1).scale(8));
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
						it.unimi.dsi.fastutil.ints.IntList affectedEntities = new it.unimi.dsi.fastutil.ints.IntArrayList(2);
						if (setAttackTarget(target, closest)) {
							affectedEntities.add(target.getId());
						}
						if (setAttackTarget(closest, target)) {
							affectedEntities.add(closest.getId());
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

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CHauntingActionPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send() {
		KPacketSender.sendToServer(new CHauntingActionPacket());
	}

	public static boolean setAttackTarget(LivingEntity entity, LivingEntity target) {
		Brain<?> brain = entity.getBrain();
		if (brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
			brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
		}
		if (brain.hasMemoryValue(MemoryModuleType.ATTACK_COOLING_DOWN)) {
			brain.setMemory(MemoryModuleType.ATTACK_COOLING_DOWN, false);
		}
		if (brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
			brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		}
		if (entity instanceof Mob mob) {
			mob.setAggressive(true);
			mob.setTarget(target);
			if (mob instanceof Warden warden) {
				warden.setAttackTarget(target);
			} else if (mob instanceof Frog frog) {
				frog.setTongueTarget(target);
			} else if (mob instanceof Goat) {
				if (brain.hasMemoryValue(MemoryModuleType.RAM_COOLDOWN_TICKS)) {
					brain.eraseMemory(MemoryModuleType.RAM_COOLDOWN_TICKS);
				}
				if (brain.hasMemoryValue(MemoryModuleType.RAM_TARGET)) {
					brain.setMemory(MemoryModuleType.RAM_TARGET, target.position());
				}
			}
			return true;
		}
		return false;
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
