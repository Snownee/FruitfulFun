package snownee.fruits;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
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
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.duck.FFBee;
import snownee.fruits.food.FoodModule;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.loader.Platform;

public final class Hooks {

	public static boolean bee;
	public static boolean food;
	public static boolean farmersdelight;
	public static boolean vac;
	public static boolean curios = Platform.isModLoaded("curios");
	public static boolean supplementaries = Platform.isModLoaded("supplementaries");
	public static boolean jade = Platform.isModLoaded("jade");

	private Hooks() {
	}

	public static Predicate<BlockState> wrapPollinationPredicate(Predicate<BlockState> original) {
		return state -> {
			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				return false;
			}
			if (state.getBlock() instanceof FruitLeavesBlock block) {
				if (block instanceof CherryLeavesBlock) {
					return block.notPlacedByPlayer(state); // not placed by player
				}
				if (!block.canGrow(state)) {
					return false;
				}
				return state.getValue(FruitLeavesBlock.AGE) == 2;
			} else if (state.getBlock() instanceof LeavesBlock && state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) {
				return false;
			}
			return original.test(state);
		};
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
		if (BeeModule.INSPECTOR.is(held)) {
			return InteractionResult.PASS;
		}
		if (held.is(Items.DEBUG_STICK)) {
			if (!player.level().isClientSide) {
				// add debug code here
//				attributes.setTexture(new ResourceLocation(FruitfulFun.ID, "pink_bee"));
//				attributes.getLocus(Allele.FANCY).setData((byte) 0x22);
				attributes.getLocus(Allele.FEAT2).setData((byte) 0x11);
				attributes.getPollens().add("fruitfulfun:apple_leaves");
				attributes.getPollens().add("wither_rose");
				attributes.updateTraits(bee);
			}
			return InteractionResult.CONSUME;
		}
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
			babyAttributes.breedFrom(
					BeeAttributes.of(parent1),
					mutagenAffectedAllele(parent1),
					BeeAttributes.of(parent2),
					mutagenAffectedAllele(parent2),
					baby);
		}
	}

	private static Allele mutagenAffectedAllele(Bee bee) {
		MobEffectInstance effect = bee.getEffect(BeeModule.MUTAGEN_EFFECT.get());
		if (effect == null) {
			return null;
		}
		return Allele.byIndex(effect.getAmplifier());
	}

	public static Vec3 modifyExplosionDeltaMovement(Entity entity, double dx, double dy, double dz, float radius) {
		Vec3 deltaMovement = entity.getDeltaMovement();
		dx = dx * radius * 0.5;
		if (Math.abs(deltaMovement.x + dx) > 3) {
			dx = Mth.clamp(deltaMovement.x + dx, -3, 3) - deltaMovement.x;
		}
		dy = dy * radius * 0.5 + Mth.sign(dy) * 0.1;
		if (Math.abs(deltaMovement.y + dy) > 3) {
			dy = Mth.clamp(deltaMovement.y + dy, -3, 3) - deltaMovement.y;
		}
		dz = dz * radius * 0.5;
		if (Math.abs(deltaMovement.z + dz) > 3) {
			dz = Mth.clamp(deltaMovement.z + dz, -3, 3) - deltaMovement.z;
		}
		return new Vec3(dx, dy, dz);
	}

	public static void appendEffectTooltip(Item item, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (!Platform.isPhysicalClient()) {
			return; // we don't want to access client config class on server
		}
		if (FFClientConfig.foodSpecialEffectTooltip && shouldClearHarmfulEffects(item)) {
			tooltip.add(Component.translatable("tip.fruitfulfun.clearHarmfulEffects").withStyle(ChatFormatting.BLUE));
		}
		FoodProperties properties = item.getFoodProperties();
		if (FFClientConfig.foodStatusEffectTooltip && properties != null) {
			List<MobEffectInstance> effects = properties.getEffects().stream()
					.filter($ -> $.getFirst() != null && $.getSecond() > 0)
					.map(Pair::getFirst)
					.toList();
			if (!effects.isEmpty()) {
				PotionUtils.addPotionTooltip(effects, tooltip, 1.0F);
			}
		}
	}

	public static boolean shouldClearHarmfulEffects(Item item) {
		return food && (!farmersdelight || !Platform.isProduction()) && FoodModule.HONEY_POMELO_TEA.get().asItem() == item;
	}
}
