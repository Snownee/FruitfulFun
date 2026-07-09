package snownee.fruits.bee;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFPlayer;

public class HauntingManager {
	@Nullable
	public final Entity target;
	public final boolean isGhostBee;
	@Nullable
	public CompoundTag storedBee;
	private ImmutableSet<Trait> traits = ImmutableSet.of();
	private int fireCounter;
	private long lastDamage;
	private long ticks;
	private int advancementCounter;

	public HauntingManager(@Nullable Entity target) {
		this.target = target;
		isGhostBee = target instanceof Bee && BeeAttributes.of(target).hasTrait(Trait.GHOST);
		if (isGhostBee) {
			target.stopRiding();
		}
	}

	public void hurtInFire(ServerPlayer player) {
		if (player.level().getGameTime() - lastDamage < 30) {
			if (++fireCounter >= 4) {
				getExorcised(player);
			}
		} else {
			fireCounter = 0;
		}
		lastDamage = player.level().getGameTime();
	}

	public void getExorcised(ServerPlayer player) {
		player.setCamera(null);
		if (isGhostBee) {
			addNegativeEffects((LivingEntity) target);
		}
		respawnStoredBee(player);
		addNegativeEffects(player);
		player.level().playSound(null, player, BeeModule.STOP_HAUNTING.get(), player.getSoundSource(), 1, 1);
	}

	private static void addNegativeEffects(LivingEntity entity) {
		if (FFCommonConfig.hauntingCooldownSeconds <= 0) {
			return;
		}
		int ticks = FFCommonConfig.hauntingCooldownSeconds * 20;
		entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, 1));
		entity.addEffect(new MobEffectInstance(CoreModule.FRAGILITY.holderOrThrow(), ticks, 1));
	}

	public void tick(ServerPlayer player) {
		if (target == null) {
			getExorcised(player);
			FFPlayer.of(player).fruits$ensureCamera();
			return;
		}
		if (++ticks > FFCommonConfig.hauntingGhostBeeTimeLimitTicks && FFCommonConfig.hauntingGhostBeeTimeLimitTicks > 0 && isGhostBee) {
			getExorcised(player);
		}
		if (ticks % 20 == 0) {
			// let the client know the new location so the chunks can render normally
			player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
		}
	}

	public void respawnStoredBee(ServerPlayer player) {
		if (storedBee == null) {
			return;
		}
		ServerLevel level = player.level();
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(player.problemPath(), FruitfulFun.LOGGER)) {
			EntityType.create(
							TagValueInput.create(reporter.forChild(() -> ".huanted"), level.registryAccess(), storedBee),
							level,
							EntitySpawnReason.LOAD)
					.ifPresent(entity -> {
						entity.setPos(player.getX(), player.getY() + 0.7F, player.getZ());
						addNegativeEffects((LivingEntity) entity);
						level.addWithUUID(entity);
					});
		}
		storedBee = null;
		traits = ImmutableSet.of();
	}

	public void storeBee(Bee bee) {
		if (bee.level().isClientSide()) {
			return;
		}
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(bee.problemPath(), FruitfulFun.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(reporter, bee.registryAccess());
			bee.saveWithoutId(output);
			output.putString("id", Objects.requireNonNull(bee.getEncodeId()));
			traits = ImmutableSet.copyOf(BeeAttributes.of(bee).genes().traits());
			storedBee = output.buildResult();
			bee.discard();
		}
	}

	public boolean hasTrait(Trait trait) {
		return traits.contains(trait);
	}

	public void performPinkSkill() {
		if (advancementCounter == 0 && target != null && target.getType() == EntityType.RAVAGER) {
			advancementCounter = 1;
		}
	}

	public void onRavagerKill(Player player) {
		if (advancementCounter > 0 && ++advancementCounter == 6) {
			Hooks.awardSimpleAdvancement(player, "haunting_skill");
		}
	}
}
