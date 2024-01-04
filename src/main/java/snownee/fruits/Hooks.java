package snownee.fruits;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.FFBee;
import snownee.fruits.bee.HybridizingContext;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.Util;

public final class Hooks {

	public static boolean bee;
	public static boolean food;
	public static boolean cherry;
	public static boolean farmersdelight = Platform.isModLoaded("farmersdelight");

	private Hooks() {
	}

	public static boolean safeSetBlock(Level world, BlockPos pos, BlockState state) {
		BlockState old = world.getBlockState(pos);
		if (old == state || old.hasBlockEntity() || old.getPistonPushReaction() == PushReaction.BLOCK || old.getBlock() == Blocks.OBSIDIAN || old.is(BlockTags.WITHER_IMMUNE)) {
			return false;
		}
		return world.setBlockAndUpdate(pos, state);
	}

	public static boolean canPollinate(BlockState state) {
		if (state.is(BlockTags.TALL_FLOWERS)) {
			if (state.getBlock() == Blocks.SUNFLOWER) {
				return state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
			} else {
				return true;
			}
		} else if (state.is(BlockTags.SMALL_FLOWERS)) {
			return true;
		} else if (bee && state.getBlock() instanceof FruitLeavesBlock) {
			if (!((FruitLeavesBlock) state.getBlock()).canGrow(state)) {
				return false;
			}
			return state.getValue(FruitLeavesBlock.AGE) == 2;
		} else {
			return false;
		}
	}

	public static void onPollinateComplete(Bee bee) {
		BlockPos savedFlowerPos = bee.getSavedFlowerPos();
		if (savedFlowerPos == null) {
			return;
		}
		Level level = bee.level();
		final BlockState state = level.getBlockState(savedFlowerPos);
		Block block = state.getBlock();
		String newPollen = Util.trimRL(BuiltInRegistries.BLOCK.getKey(block));
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		pollens.remove(newPollen);
		if (pollens.isEmpty()) {
			pollens.add(newPollen);
			return;
		}
		pollens.add(newPollen);
		Optional<HybridizingRecipe> recipe = level.getRecipeManager().getRecipeFor(BeeModule.RECIPE_TYPE, new HybridizingContext(attributes), level);
		if (recipe.isPresent()) {
			Block newBlock = recipe.get().getResult(attributes);
			BlockState newState = newBlock.defaultBlockState();
			boolean isLeaves = newBlock instanceof FruitLeavesBlock;
			boolean isFlower = !isLeaves && newState.is(BlockTags.FLOWERS);
			boolean isMisc = !isLeaves && !isFlower;
			if (!isMisc && (isLeaves != (block instanceof FruitLeavesBlock))) {
				return;
			}
			BlockPos root = savedFlowerPos;
			if (state.is(BlockTags.TALL_FLOWERS) && state.hasProperty(DoublePlantBlock.HALF) && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
				root = root.below();
			} else if (isMisc && !newBlock.isPossibleToRespawnInThis(newState) && !(block instanceof FruitLeavesBlock)) {
				root = root.below();
			}
			boolean isBigFlower = false;
			if (isLeaves) {
				newState = newState.setValue(FruitLeavesBlock.AGE, 2);
				newState = newState.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE));
			} else if (isFlower) {
				if (newState.is(BlockTags.TALL_FLOWERS) && newState.hasProperty(DoublePlantBlock.HALF)) {
					newState = newState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
					isBigFlower = true;
				}
			}
			boolean placed = safeSetBlock(level, root, newState);
			if (placed && isBigFlower) {
				newState = newState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
				safeSetBlock(level, root.above(), newState);
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, root.above(), 0); // bonemeal effects
			}
			if (placed) {
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, root, 0);
				pollens.clear();
				return;
			}
		}
		/* off */
        if (pollens.size() > 3) {
            int toRemove = pollens.size() - 3;
            while (toRemove --> 0) {
				pollens.remove(0);
            }
        }
		pollens.add(newPollen);
        /* on */
	}

	public static void modifyRayTraceResult(HitResult hitResult, Consumer<HitResult> consumer) {
		if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
			return;
		}
		Entity entity = ((EntityHitResult) hitResult).getEntity();
		if (!CoreModule.SLIDING_DOOR.is(entity.getType())) {
			return;
		}
		Vec3 vec = hitResult.getLocation();
		BlockPos pos = entity.blockPosition();
		if (vec.y - pos.getY() >= 1)
			pos = pos.above();
		AABB intersection = entity.getBoundingBox().intersect(new AABB(pos));
		vec = intersection.getCenter();
		//mc.level.addParticle(ParticleTypes.ANGRY_VILLAGER, vec.x, vec.y, vec.z, 0, 0, 0);
		consumer.accept(new BlockHitResult(vec, Direction.UP, pos, false));
	}

	public static void hornHarvest(ServerLevel level, ServerPlayer player) {
		Vec3 eye = player.getEyePosition();
		BlockPos eyePos = BlockPos.containing(eye);
		long count = level.getPoiManager().findAll(
						$ -> $.is(CoreModule.POI_TYPE),
						Predicates.alwaysTrue(),
						eyePos,
						24,
						PoiManager.Occupancy.ANY)
				.flatMap($ -> level.getBlockEntity($, CoreModule.FRUIT_TREE.get()).stream())
				.peek($ -> hornHarvest(level, player, $, eyePos, null))
				.count();
		if (count > 0) {
			Advancement advancement = level.getServer().getAdvancements().getAdvancement(new ResourceLocation("husbandry/fruitfulfun/horn"));
			if (advancement != null) {
				player.getAdvancements().award(advancement, "_");
			}
		}
	}

	private static void hornHarvest(ServerLevel level, ServerPlayer player, FruitTreeBlockEntity core, BlockPos eyePos, Consumer<ItemEntity> consumer) {
		Set<BlockPos> leaves = core.getActiveLeaves();
		BlockPos corePos = core.getBlockPos();
		if (leaves.isEmpty()) {
			BlockState blockState = level.getBlockState(eyePos);
			if (blockState.getBlock() instanceof FruitLeavesBlock) {
				Iterable<BlockPos> posList = BlockPos.betweenClosed(corePos.offset(-3, -1, -3), corePos.offset(3, 2, 3));
				FruitLeavesBlock.rangeDrop(level, posList, 0, core, consumer);
			}
		} else {
			for (BlockPos pos : leaves) {
				pos = corePos.offset(pos);
				BlockState blockState = level.getBlockState(pos);
				if (!(blockState.getBlock() instanceof FruitLeavesBlock)) {
					continue;
				}
				ItemEntity itemEntity = FruitLeavesBlock.dropFruit(level, pos, blockState, core, 0);
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
		Saddleable saddleable = (Saddleable) bee;
		ItemStack held = player.getItemInHand(hand);
		if (saddleable.isSaddled()) {
			boolean trusted = player.isCreative() || attributes.trusts(player.getUUID());
			if (CommonProxy.isShears(held)) {
				if (!trusted) {
					((FFBee) bee).fruits$roll();
					return InteractionResult.FAIL;
				}
				ItemStack saddle = attributes.getSaddle();
				attributes.setSaddle(ItemStack.EMPTY);
				if (!player.level().isClientSide) {
					held.hurtAndBreak(1, player, $ -> $.broadcastBreakEvent(hand));
					bee.spawnAtLocation(saddle);
					bee.gameEvent(GameEvent.SHEAR, player);
					bee.level().playSound(null, bee, BeeModule.BEE_SHEAR.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
				}
				return InteractionResult.sidedSuccess(player.level().isClientSide);
			} else if (!bee.isVehicle() && !player.isSecondaryUseActive()) {
				if (!trusted) {
					((FFBee) bee).fruits$roll();
					return InteractionResult.FAIL;
				}
				if (!player.level().isClientSide) {
					player.startRiding(bee);
				}
				return InteractionResult.sidedSuccess(player.level().isClientSide);
			}
		} else if (held.is(Items.SADDLE) && saddleable.isSaddleable()) {
			if (!player.level().isClientSide) {
				saddleable.equipSaddle(SoundSource.NEUTRAL);
				attributes.setSaddle(held.split(1));
				player.level().gameEvent(bee, GameEvent.EQUIP, bee.position());
			}
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		return InteractionResult.PASS;
	}

	public static Vec3 getRiddenInput(Bee bee, Player player, Vec3 vec3) {
		Level level = bee.level();
		if (level.dimensionType().ultraWarm()) {
			return new Vec3(0, -0.07, 0);
		}
		BeeAttributes attributes = BeeAttributes.of(bee);
		if (!attributes.hasTrait(Trait.RAIN_CAPABLE) && level.isRainingAt(bee.blockPosition())) {
			return new Vec3(0, -0.07, 0);
		}
		float x = player.xxa * 0.5f;
		float z = player.zza;
		z *= bee.onGround() ? 0.3f : 0.6f;
		if (z <= 0.0f) {
			z *= 0.25f;
		}
		double y = 0;
		if (tooFarFromSurface(level, bee.blockPosition())) {
			y = -0.07;
		} else if (player.isLocalPlayer() && ((LocalPlayer) player).input.jumping) {
			y = 0.1;
		} else if (x != 0 || z != 0) {
			y = Mth.clamp(player.getLookAngle().y * 0.5, -0.1, 0.1);
		}
		if (y >= 0) {
			BlockPos pos = BlockPos.containing(player.getEyePosition());
			if (level.getBlockState(pos).isSuffocating(level, pos)
					|| level.getBlockState(pos.above()).isSuffocating(level, pos)) {
				y = -0.07;
			}
		}
		return new Vec3(x, y, z);
	}

	private static final int[] SURFACE_CHECKS = {15, 11, 7, 3, 14, 10, 6, 2, 13, 9, 5, 1, 12, 8, 4, 0};

	public static boolean tooFarFromSurface(Level level, BlockPos pos) {
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
			loveCause1.awardStat(BeeModule.BEE_ONE_CM);
		}
		ServerPlayer loveCause2 = parent2.getLoveCause();
		if (loveCause2 != null) {
			builder.add(loveCause2.getUUID());
			if (loveCause1 != loveCause2) {
				loveCause2.awardStat(BeeModule.BEE_ONE_CM);
			}
		}
		babyAttributes.setTrusted(builder.build());
		babyAttributes.breedFrom(
				BeeAttributes.of(parent1),
				mutagenAffectedAllele(parent1),
				BeeAttributes.of(parent2),
				mutagenAffectedAllele(parent2),
				baby);
	}

	private static Allele mutagenAffectedAllele(Bee bee) {
		MobEffectInstance effect = bee.getEffect(BeeModule.MUTAGEN_EFFECT.get());
		if (effect == null) {
			return null;
		}
		return Allele.byIndex(effect.getAmplifier());
	}
}
