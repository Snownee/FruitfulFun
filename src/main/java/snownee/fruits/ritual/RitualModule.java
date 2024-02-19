package snownee.fruits.ritual;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.food.FoodModule;
import snownee.fruits.util.CommonProxy;
import snownee.fruits.vacuum.VacModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("ritual")
@KiwiModule.Optional
public class RitualModule extends AbstractModule {
	public static final KiwiGO<SoundEvent> RITUAL_FINISH = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(
			FruitfulFun.ID,
			"block.ritual.finish")));
	public static final Supplier<BlockPattern> RITUAL = Suppliers.memoize(() -> BlockPatternBuilder.start()
			.aisle(
					" C C ",
					"C~~~C",
					" ~ ~ ",
					"C~~~C",
					" C C ")
			.aisle(
					"C~~~C",
					"~~~~~",
					"~~~~~",
					"~~~~~",
					"C~~~C")
			.where('C', BlockInWorld.hasState(CommonProxy::isLitCandle))
			.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
			.build());
	public static final UUID DUMMY_UUID = UUID.fromString("46fee2cc-dbea-4a2f-9768-afb958387795");
	public static final String INTERACTION_NAME = "@FruitfulFun";
	public static final int LIFETIME = 100;

	public RitualModule() {
		Hooks.ritual = true;
	}

	public static void tryStartRitual(Level level, BlockPos pos) {
		if (!level.getEntities(EntityType.INTERACTION, new AABB(pos), RitualModule::isFFInteractionEntity).isEmpty()) {
			return;
		}
		List<BlockPos> heads = findRitual(level, pos);
		if (heads.isEmpty()) {
			return;
		}
		for (BlockPos head : heads) {
			level.playSound(null, head, SoundEvents.ENDER_DRAGON_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F);
		}
		Interaction interaction = Objects.requireNonNull(EntityType.INTERACTION.create(level));
		interaction.setPos(Vec3.atCenterOf(pos));
		interaction.setCustomName(Component.literal(INTERACTION_NAME));
		level.addFreshEntity(interaction);
	}

	public static List<BlockPos> findRitual(Level level, BlockPos pos) {
		BlockPattern pattern = RITUAL.get();
		BlockPos.MutableBlockPos mutable = pos.mutable()
				.move(Direction.NORTH, 2)
				.move(Direction.EAST, 2);
		BlockPattern.BlockPatternMatch match = pattern.matches(level, mutable, Direction.UP, Direction.NORTH);
		if (match == null) {
			return List.of();
		}
		List<BlockPos> headPosList = Lists.newArrayListWithExpectedSize(4);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			mutable.set(pos).move(Direction.UP).move(direction, 4);
			BlockState head = level.getBlockState(mutable);
			if (!head.is(Blocks.DRAGON_HEAD) && !head.is(Blocks.DRAGON_WALL_HEAD)) {
				continue;
			}
			Direction facing = getSkullFacing(head);
			if (facing != null) {
				headPosList.add(mutable.immutable());
			}
		}
		return headPosList;
	}

	@Nullable
	public static Direction getSkullFacing(BlockState state) {
		if (state.getBlock() instanceof WallSkullBlock) {
			return state.getValue(WallSkullBlock.FACING);
		}
		if (state.getBlock() instanceof SkullBlock) {
			return switch (state.getValue(SkullBlock.ROTATION)) {
				case 0 -> Direction.NORTH;
				case 4 -> Direction.EAST;
				case 8 -> Direction.SOUTH;
				case 12 -> Direction.WEST;
				default -> null;
			};
		}
		return null;
	}

	public static boolean tickDragonHead(Level level, BlockPos blockPos, BlockState blockState, SkullBlockEntity skullBlockEntity) {
		Direction facing = getSkullFacing(blockState);
		if (facing == null) {
			return false;
		}
		BlockPos center = blockPos.mutable().move(facing, 4).move(Direction.DOWN);
		List<Interaction> entities = level.getEntities(EntityType.INTERACTION, new AABB(center), RitualModule::isFFInteractionEntity);
		if (entities.isEmpty()) {
			return false;
		}
		Interaction interaction = entities.get(0);
		skullBlockEntity.isAnimating = false; // cancel the tick lerping
		skullBlockEntity.animationTickCount = interaction.tickCount >= LIFETIME - 1 ? 0 : 2;
		if (!level.isClientSide) {
			return true;
		}
		if (interaction.tickCount % 2 != 0) {
			return true;
		}
		double x = blockPos.getX() + 0.5;
		double y = blockPos.getY() + (blockState.is(Blocks.DRAGON_WALL_HEAD) ? 0.6 : 0.3);
		double z = blockPos.getZ() + 0.5;
		Vec3 motion = interaction.position().subtract(x, y, z).normalize();
		motion = motion.yRot(-0.1f);
		double power = Mth.clamp((interaction.tickCount + 20) / 100d, 0.25, 1);
		for (int i = 0; i < 3; ++i) {
			double dx = (motion.x * 0.08 + level.random.nextGaussian() * 0.005) * power;
			double dy = (motion.y * 0.08 + level.random.nextGaussian() * 0.005) * power;
			double dz = (motion.z * 0.08 + level.random.nextGaussian() * 0.005) * power;
			for (int k = 2; k < 6; ++k) {
				level.addParticle(ParticleTypes.DRAGON_BREATH, x, y, z, dx * k, dy * k, dz * k);
			}
			motion = motion.yRot(0.1f);
		}
		return true;
	}

	public static boolean isFFInteractionEntity(Interaction interaction) {
		return interaction.getCustomName() != null && interaction.getCustomName().getString().equals(INTERACTION_NAME);
	}

	public static void tickInteraction(Interaction interaction) {
		Level level = interaction.level();
		if (level.isClientSide) {
			if (interaction.tickCount == 55) {
				level.playLocalSound(interaction.blockPosition(), RITUAL_FINISH.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
			}
			if (interaction.tickCount == LIFETIME - 1) {
				finishRitual(interaction);
			}
			return;
		}
		if (interaction.tickCount > LIFETIME) {
			interaction.discard();
			return;
		}
		if (!FoodModule.CHORUS_FRUIT_PIE.is(level.getBlockState(interaction.blockPosition()))) {
			interaction.discard();
			return;
		}
		if (interaction.tickCount == LIFETIME) {
			finishRitual(interaction);
			return;
		}
		if (!interaction.isVehicle()) {
			List<ItemEntity> itemEntities = level.getEntitiesOfClass(
					ItemEntity.class,
					interaction.getBoundingBox(),
					(ItemEntity itemEntity) -> {
						return itemEntity.isAlive() && !itemEntity.isNoGravity();
					});
			if (!itemEntities.isEmpty()) {
				ItemEntity itemEntity = itemEntities.get(0);
				itemEntity.startRiding(interaction);
				itemEntity.setPickUpDelay(LIFETIME);
			}
		}
	}

	public static void finishRitual(Interaction interaction) {
		Level level = interaction.level();
		BlockPos pos = interaction.blockPosition();
		List<BlockPos> heads = findRitual(level, pos);
		if (heads.isEmpty()) {
			return;
		}
		for (BlockPos head : heads) {
			if (level.getBlockEntity(head) instanceof SkullBlockEntity skullBlockEntity) {
				skullBlockEntity.animationTickCount = 0;
			}
		}
		if (level.isClientSide) {
			BlockState state = level.getBlockState(pos);
			VoxelShape shape = state.getCollisionShape(level, pos);
			level.removeBlock(pos, false);
			if (shape.isEmpty()) {
				return;
			}
			AABB aabb = shape.bounds();
			for (int i = 0; i < 24; i++) {
				double x = Mth.lerp(level.random.nextDouble(), aabb.minX, aabb.maxX) + pos.getX();
				double y = Mth.lerp(level.random.nextDouble(), aabb.minY, aabb.maxY) + pos.getY();
				double z = Mth.lerp(level.random.nextDouble(), aabb.minZ, aabb.maxZ) + pos.getZ();
				level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0, 0, 0);
			}
			return;
		}
		level.removeBlock(pos, false);
		ItemStack itemStack = ItemStack.EMPTY;
		if (interaction.getFirstPassenger() instanceof ItemEntity itemEntity) {
			itemStack = itemEntity.getItem();
		}
		interaction.discard();
		int breathCount = Hooks.vac && VacModule.VAC_GUN_CASING.is(itemStack) ? 1 : heads.size() + 1;
		AreaEffectCloud flame = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		flame.setRadius(0.5f * breathCount);
		flame.setDuration(200);
		flame.setParticle(ParticleTypes.DRAGON_BREATH);
		flame.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
		flame.ownerUUID = DUMMY_UUID;
		level.addFreshEntity(flame);
		level.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.AMBIENT, 1.0F, 1.0F);
		if (breathCount == 1) {
			interaction.spawnAtLocation(VacModule.VAC_GUN.itemStack());
			itemStack.shrink(1);
		}
		ServerLevel serverLevel = (ServerLevel) level;
		Advancement advancement = Hooks.advancement(serverLevel, "ritual");
		if (advancement != null) {
			for (ServerPlayer player : serverLevel.players()) {
				if (player.distanceToSqr(interaction) > 256) {
					continue;
				}
				player.getAdvancements().award(advancement, "_");
			}
		}
	}

	public static void rightClickInteraction(Interaction interaction, Player player, InteractionHand interactionHand) {
		Level level = interaction.level();
		if (level.isClientSide) {
			return;
		}
		ItemStack itemStack = player.getItemInHand(interactionHand);
		if (itemStack.isEmpty()) {
			return;
		}
		itemStack = itemStack.split(1);
		ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), itemStack.split(1));
		if (level.addFreshEntity(itemEntity)) {
			itemEntity.startRiding(interaction);
			itemEntity.setPickUpDelay(LIFETIME);
		}
	}
}
