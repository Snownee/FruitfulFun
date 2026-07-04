package snownee.fruits;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.duck.FFBee;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.mixin.EntityAccess;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.KUtil;

public final class Hooks {

	public static boolean bee;
	public static boolean food;
	public static boolean farmersdelight;
	public static boolean ritual;
	public static boolean gadget;
	public static boolean supplementaries = Platform.isModLoaded("supplementaries");
	public static boolean jade = Platform.isModLoaded("jade");
	public static boolean hauntedHarvest = Platform.isModLoaded("hauntedharvest");
	public static final Set<MobEffect> scentEffects = Sets.newHashSet();

	private Hooks() {
	}

	public static Predicate<BlockState> wrapPollinationPredicate(Predicate<BlockState> original) {
		return state -> {
			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				return false;
			}
			if (state.getBlock() instanceof FruitLeavesBlock block) {
				if (block instanceof CherryLeavesBlock) {
					return block.notPlacedByPlayer(state);
				}
				if (!block.canGrow(state)) {
					return false;
				}
				return state.getValue(FruitLeavesBlock.AGE) == FruitLeavesBlock.BLOOMING;
			} else if (state.getBlock() instanceof LeavesBlock && state.hasProperty(LeavesBlock.PERSISTENT) &&
					state.getValue(LeavesBlock.PERSISTENT)) {
				return false;
			}
			return original.test(state);
		};
	}

	public static void modifyRayTraceResult(@Nullable HitResult hitResult, Consumer<HitResult> consumer) {
		if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
			return;
		}
		Entity entity = ((EntityHitResult) hitResult).getEntity();
		if (!CoreModule.SLIDING_DOOR.is(entity.getType())) {
			return;
		}
		Vec3 vec = hitResult.getLocation();
		BlockPos pos = entity.blockPosition();
		if (vec.y - pos.getY() >= 1) {
			pos = pos.above();
		}
		AABB intersection = entity.getBoundingBox().intersect(new AABB(pos));
		vec = intersection.getCenter();
		//mc.level.addParticle(ParticleTypes.ANGRY_VILLAGER, vec.x, vec.y, vec.z, 0, 0, 0);
		consumer.accept(new BlockHitResult(vec, Direction.UP, pos, false));
	}

	public static void hornHarvest(ServerPlayer player) {
		Vec3 eye = player.getEyePosition();
		BlockPos eyePos = BlockPos.containing(eye);
		AtomicInteger count = new AtomicInteger();
		player.level().getPoiManager()
				.findAll($ -> $.is(CoreModule.POI_TYPE), Predicates.alwaysTrue(), eyePos, 24, PoiManager.Occupancy.ANY)
				.flatMap($ -> player.level().getBlockEntity($, CoreModule.FRUIT_TREE.get()).stream())
				.forEach($ -> {
					hornHarvest(player.level(), player, $, eyePos, null);
					count.incrementAndGet();
				});
		if (count.get() > 0) {
			awardSimpleAdvancement(player, "horn");
		}
	}

	public static void awardSimpleAdvancement(Player player, String id) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		AdvancementHolder advancement = advancement(serverPlayer.level(), id);
		if (advancement != null) {
			serverPlayer.getAdvancements().award(advancement, "_");
		}
	}

	@Nullable
	public static AdvancementHolder advancement(ServerLevel level, String id) {
		return level.getServer().getAdvancements().get(Identifier.parse("husbandry/fruitfulfun/" + id));
	}

	private static void hornHarvest(
			ServerLevel level,
			ServerPlayer player,
			FruitTreeBlockEntity core,
			BlockPos eyePos,
			@Nullable Consumer<ItemEntity> consumer) {
		List<BlockPos> leaves = core.getLeaves();
		BlockPos corePos = core.getBlockPos();
		if (leaves.isEmpty()) {
			BlockState blockState = level.getBlockState(eyePos);
			if (blockState.getBlock() instanceof FruitLeavesBlock) {
				Iterable<BlockPos> posList = BlockPos.betweenClosed(corePos.offset(-3, -1, -3), corePos.offset(3, 2, 3));
				FruitLeavesBlock.rangeDrop(level, posList, core, consumer);
			}
		} else {
			for (BlockPos pos : leaves) {
				BlockState blockState = level.getBlockState(pos);
				if (!(blockState.getBlock() instanceof FruitLeavesBlock leavesBlock)) {
					continue;
				}
				ItemEntity itemEntity = leavesBlock.dropFruit(level, pos, blockState, core);
				if (itemEntity != null && consumer != null) {
					consumer.accept(itemEntity);
				}
			}
		}
		corePos = corePos.below();
		double dist = Math.sqrt(corePos.distSqr(eyePos));
		BlockPositionSource dest = new BlockPositionSource(corePos);
		Vec3 eye = player.getEyePosition();
		level.sendParticles(new VibrationParticleOption(dest, Math.max((int) (dist / 2), 4)), eye.x, eye.y + 1, eye.z, 1, 0, 0, 0, 0);
	}

	public static InteractionResult playerInteractBee(Player player, InteractionHand hand, Bee bee) {
		BeeAttributes attributes = BeeAttributes.of(bee);
		ItemStack held = player.getItemInHand(hand);
		if (BeeModule.INSPECTOR.is(held)) {
			return InteractionResult.PASS;
		}
		boolean isClientSide = player.level().isClientSide();
		if (held.is(Items.DEBUG_STICK)) {
			if (!isClientSide) {
				boolean hasPink = attributes.hasTrait(Trait.PINK);
				boolean hasGhost = attributes.hasTrait(Trait.GHOST);
				// add debug code here
				attributes.getLocus(Allele.FANCY).setData((byte) 0x11);
				attributes.getLocus(Allele.FEAT1).setData((byte) 0x22);
				attributes.getLocus(Allele.FEAT2).setData((byte) 0x22);
				attributes.getLocus(Allele.RAINC).setData((byte) 0x11);
				attributes.getPollens().add("fruitfulfun:apple_leaves");
				attributes.getPollens().add("wither_rose");
				if (hasPink) {
					GeneData genes = attributes.getGenes();
					if (hasGhost) {
						genes.removeExtraTrait(Trait.GHOST);
					} else {
						genes.addExtraTrait(Trait.GHOST);
					}
				}
				attributes.updateTraits(bee);
				var textures = List.of(FruitfulFun.id("pink_bee"), FruitfulFun.id("ghost_bee"), FruitfulFun.id("wither_bee"));
				attributes.setTexture(textures.get(bee.getRandom().nextInt(textures.size())));
				attributes.addTrusted(player.getUUID());
			}
			return InteractionResult.CONSUME;
		}
		if (bee.isSaddled()) {
			boolean trusted = player.isCreative() || attributes.trusts(player.getUUID());
			if (Platform.isShearsRightClickable(held)) {
				if (!trusted) {
					((FFBee) bee).fruits$roll();
					return InteractionResult.FAIL;
				}
				if (!isClientSide) {
					held.hurtAndBreak(1, player, hand);
					attributes.dropSaddle(bee);
					bee.gameEvent(GameEvent.SHEAR, player);
					bee.level().playSound(null, bee, BeeModule.BEE_SHEAR.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
				}
				return InteractionResult.SUCCESS_SERVER;
			} else if (!bee.isVehicle() && !player.isSecondaryUseActive()) {
				if (!trusted) {
					((FFBee) bee).fruits$roll();
					return InteractionResult.FAIL;
				}
				if (!isClientSide) {
					player.startRiding(bee);
				}
				return InteractionResult.SUCCESS_SERVER;
			}
		} else if (bee.isEquippableInSlot(held, EquipmentSlot.SADDLE)) {
			return held.interactLivingEntity(player, bee, hand);
		}
		if (FFCommonConfig.hauntingEnabled && attributes.hasTrait(Trait.GHOST) && !Platform.isFakePlayer(player)) {
			if (!isClientSide && (FFCommonConfig.hauntingCooldownSeconds <= 0 || !bee.hasEffect(CoreModule.FRAGILITY.holderOrThrow()))) {
				FFPlayer.of(player).fruits$setHauntingTarget(bee);
			}
			return InteractionResult.SUCCESS_SERVER;
		}
		return InteractionResult.PASS;
	}

	public static Vec3 getRiddenInput(Bee bee, Player player, Vec3 vec3) {
		Level level = bee.level();
		BeeAttributes attributes = BeeAttributes.of(bee);
		boolean ghost = attributes.hasTrait(Trait.GHOST);
		if (FFCommonConfig.beeRidingEnvironmentAttrRules && !ghost && !level.environmentAttributes().getValue(
				BeeModule.BEE_RIDEABLE.get(),
				bee.position())) {
			return new Vec3(0, -0.07, 0);
		}
		if (FFCommonConfig.beeRidingRainingLimit && !attributes.hasTrait(Trait.RAIN_CAPABLE) && level.isRainingAt(bee.blockPosition())) {
			return new Vec3(0, -0.07, 0);
		}
		float x = player.xxa * 0.7f * FFCommonConfig.beeRidingHorizontalSpeedMultiplier;
		float z = player.zza * 1.4f * FFCommonConfig.beeRidingHorizontalSpeedMultiplier;
//		FruitfulFun.LOGGER.info("player.xxa={}, player.zza={}", player.xxa, player.zza);
		z *= bee.onGround() ? 0.3f : 0.6f;
		if (z <= 0.0f) {
			z *= 0.25f;
		}
		double y = 0;
		if (!ghost && tooFarFromSurface(level, bee.blockPosition())) {
			y = -0.07;
		} else if (player.isLocalPlayer() && ((LocalPlayer) player).input.keyPresses.jump()) {
			y = 0.1;
		} else if (x != 0 || z != 0) {
			y = Mth.clamp(player.getLookAngle().y * 0.5, -0.1, 0.1);
		}
		if (y >= 0) {
			BlockPos pos = BlockPos.containing(player.getEyePosition());
			if (level.getBlockState(pos).isSuffocating(level, pos) || level.getBlockState(pos.above()).isSuffocating(level, pos)) {
				y = -0.07;
			}
		}
		y *= FFCommonConfig.beeRidingVerticalSpeedMultiplier;
		return new Vec3(x, y, z);
	}

	private static final int[] SURFACE_CHECKS = {15, 11, 7, 3, 14, 10, 6, 2, 13, 9, 5, 1, 12, 8, 4, 0};

	public static boolean tooFarFromSurface(Level level, BlockPos pos) {
		if (!FFCommonConfig.beeRidingHeightLimit) {
			return false;
		}
		if (level.getBiome(pos).is(BeeModule.UNLIMITED_BEE_RIDING)) {
			return false;
		}
		int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
		if (pos.getY() > height) {
			return pos.getY() - height > 15;
		}
		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (int i : SURFACE_CHECKS) {
			mutable.setY(pos.getY() - i);
			//noinspection deprecation
			if (level.getBlockState(mutable).blocksMotion()) {
				return false;
			}
		}
		return true;
	}

	public static void spawnBeeFromBreeding(Bee parent1, Bee parent2, Bee baby) {
		BeeAttributes babyAttributes = BeeAttributes.of(baby);
		ImmutableList.Builder<UUID> builder = ImmutableList.builder();
		ServerPlayer loveCause1 = parent1.getLoveCause();
		if (loveCause1 != null) {
			builder.add(loveCause1.getUUID());
			loveCause1.awardStat(BeeModule.BEES_BRED);
		}
		ServerPlayer loveCause2 = parent2.getLoveCause();
		if (loveCause2 != null) {
			builder.add(loveCause2.getUUID());
			if (loveCause1 != loveCause2) {
				loveCause2.awardStat(BeeModule.BEES_BRED);
			}
		}
		babyAttributes.setTrusted(builder.build());
		if (bee) {
			babyAttributes.getGenes().breedFrom(
					BeeAttributes.of(parent1).getGenes(),
					mutagenAffectedAllele(parent1),
					BeeAttributes.of(parent2).getGenes(),
					mutagenAffectedAllele(parent2),
					baby.getRandom());
			babyAttributes.updateTraits(baby);
		}
	}

	private static @Nullable Allele mutagenAffectedAllele(Bee bee) {
		MobEffectInstance effect = bee.getEffect(BeeModule.MUTAGEN_EFFECT.holderOrThrow());
		if (effect == null) {
			return null;
		}
		return Allele.byIndex(effect.getAmplifier());
	}

	public static Vec3 modifyExplosionDeltaMovement(Entity entity, Vec3 impulse, float radius) {
		Vec3 deltaMovement = entity.getDeltaMovement();
		double dx = impulse.x * radius * 0.5;
		if (Math.abs(deltaMovement.x + dx) > 3) {
			dx = Mth.clamp(deltaMovement.x + dx, -3, 3) - deltaMovement.x;
		}
		double dy = impulse.y * radius * 0.5 + Mth.sign(impulse.y) * 0.1;
		if (Math.abs(deltaMovement.y + dy) > 3) {
			dy = Mth.clamp(deltaMovement.y + dy, -3, 3) - deltaMovement.y;
		}
		double dz = impulse.z * radius * 0.5;
		if (Math.abs(deltaMovement.z + dz) > 3) {
			dz = Mth.clamp(deltaMovement.z + dz, -3, 3) - deltaMovement.z;
		}
		return new Vec3(dx, dy, dz);
	}

	public static Vec3 calculateViewVector(Entity entity1, Entity entity2, float partialTicks) {
		return ((EntityAccess) entity1).callCalculateViewVector(
				Mth.clamp(entity1.getViewXRot(partialTicks) + entity2.getViewXRot(partialTicks), -90F, 90F),
				entity1.getViewYRot(partialTicks) + entity2.getViewYRot(partialTicks));
	}

	public static void debugInChat(Player player, String msg) {
		if (Platform.isProduction()) {
			return;
		}
		String stackTrace = ExceptionUtils.getStackTrace(new Throwable());
		player.sendSystemMessage(KUtil.clickToCopy(Component.literal(msg), Component.literal(stackTrace), stackTrace));
	}
}
