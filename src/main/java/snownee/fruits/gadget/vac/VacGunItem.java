package snownee.fruits.gadget.vac;

import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.kiwi.util.PreventUpdateAnimation;

public class VacGunItem extends ProjectileWeaponItem implements PreventUpdateAnimation {
	public static final int MAX_ITEM_COUNT = 16;
	private static final int ITEM_BAR_COLOR = 0x66FF66;
	private static final ThreadLocal<ItemEntity> DUMMY_ITEM_ENTITY = new ThreadLocal<>();

	public VacGunItem(Item.Properties properties) {
		super(properties.stacksTo(1).rarity(Rarity.RARE));
	}

	public static void shoot(Player player, InteractionHand hand) {
//		ItemStack gun = player.getItemInHand(hand);
//		if (player.getCooldowns().isOnCooldown(gun.getItem())) {
//			return;
//		}
//		String ammoType = getAmmoType(gun);
//		if (ammoType == null) {
//			return;
//		}
//		if ("item".equals(ammoType)) {
//			VacGunContainer container = readItemContainer(gun);
//			if (container == null || container.isEmpty()) {
//				return;
//			}
//			VacItemProjectile projectile = shootItem(player, gun, container);
//			if (projectile != null && (
//					PomegranateModule.POMEGRANATE_ITEM.is(projectile.getItem()) ||
//							PomegranateModule.ENCHANTED_POMEGRANATE.is(projectile.getItem()))) {
//				player.getCooldowns().addCooldown(gun.getItem(), 10);
//			} else {
//				long gameTime = player.level().getGameTime();
//				long lastShot = gun.getOrCreateTag().getLong("LastShot");
//				player.getCooldowns().addCooldown(gun.getItem(), gameTime - lastShot < 8 ? 2 : 4);
//			}
//		} else if ("fluid".equals(ammoType)) {
//			// TODO
//		}
//		if (player.isLocalPlayer()) {
//			CGunShotPacket.send();
//		} else {
//			gun.getOrCreateTag().putLong("LastShot", player.level().getGameTime());
//		}
	}

	//
//	@Nullable
//	public static VacItemProjectile shootItem(Player player, ItemStack gun, VacGunContainer container) {
//		if (player.level().isClientSide()) {
//			return null;
//		}
//		ItemStack itemStack = container.getLastItem();
//		if (itemStack.isEmpty()) {
//			return null;
//		}
//		ItemStack split = itemStack.split(1);
//		Vec3 lookAngle = player.getLookAngle();
//		double d = player.getEyeY() - 0.6;
//		VacItemProjectile projectile = Objects.requireNonNull(GadgetModule.ITEM_PROJECTILE.get().create(player.level()));
//		projectile.setPos(player.getX(), d, player.getZ());
//		projectile.setOwner(player);
//		projectile.setItem(split);
//		projectile.shoot(lookAngle.x(), lookAngle.y(), lookAngle.z(), 2.0F, 0.01F);
//		player.level().addFreshEntity(projectile);
//		container.setChanged();
//		saveItemContainer(gun, container);
//		RandomSource random = player.getRandom();
//		player.level()
//				.playSound(
//						null, player, GadgetModule.GUN_SHOOT_ITEM.get(), player.getSoundSource(), 0.2f,
//						((random.nextFloat() - random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
//		return projectile;
//	}
//
//	public static int getItemCount(ItemStack gun) {
//		if (gun.getTag() == null) {
//			return 0;
//		}
//		return gun.getTag().getInt("ItemCount");
//	}

	public static void collectItem(Player player, ItemEntity itemEntity, ItemStack gun, final @Nullable VacGunContainer container) {
//		if (player.level().isClientSide()) {
//			return;
//		}
//		String ammoType = getAmmoType(gun);
//		if (ammoType != null && !"item".equals(ammoType)) {
//			return;
//		}
//		if (gun.getTag() != null && getItemCount(gun) >= MAX_ITEM_COUNT) {
//			return;
//		}
//		ItemStack itemStack = itemEntity.getItem();
//		VacGunContainer container2 = container;
//		if (container2 == null) {
//			container2 = readItemContainer(gun);
//		}
//		if (container2 == null) {
//			return;
//		}
//		int count = itemStack.getCount();
//		itemStack = container2.addItem(itemStack);
//		count = count - itemStack.getCount();
//		if (count > 0) {
//			player.take(itemEntity, count);
//		}
//		if (itemStack.isEmpty()) {
//			itemEntity.discard();
//		} else {
//			itemEntity.setItem(itemStack);
//		}
//		if (container == null) {
//			saveItemContainer(gun, container2);
//		}
	}

//	public static @Nullable VacGunContainer readItemContainer(ItemStack gun) {
//		String ammoType = getAmmoType(gun);
//		if (ammoType != null && !"item".equals(ammoType)) {
//			return null;
//		}
//		VacGunContainer container = new VacGunContainer(16);
//		if (gun.getTag() != null) {
//			container.fromTag(gun.getTag().getList("Items", Tag.TAG_COMPOUND));
//		}
//		return container;
//	}
//
//	public static void saveItemContainer(ItemStack gun, VacGunContainer container) {
//		CompoundTag tag = gun.getOrCreateTag();
//		if (container.isEmpty()) {
//			tag.remove("AmmoType");
//			tag.remove("Items");
//			tag.remove("ItemCount");
//			return;
//		}
//		tag.putString("AmmoType", "item");
//		tag.put("Items", container.createTag());
//		tag.putInt("ItemCount", container.getItemCount());
//	}
//
//	@Nullable
//	public static String getAmmoType(ItemStack gun) {
//		if (gun.getTag() == null) {
//			return null;
//		}
//		String ammoType = gun.getTag().getString("AmmoType");
//		return ammoType.isEmpty() ? null : ammoType;
//	}
//
//	@Override
//	public int getUseDuration(ItemStack itemStack, LivingEntity user) {
//		return 72000;
//	}
//
//	@Override
//	public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
//		return ItemUseAnimation.SPYGLASS;
//	}
//
//	@Override
//	public void onUseTick(Level level, LivingEntity living, ItemStack gun, int i) {
//		if (level.isClientSide()) {
//			return;
//		}
//		if (!(living instanceof Player player)) {
//			return;
//		}
//		Vec3 start = player.getEyePosition(1);
//		Vec3 lookAngle = player.getViewVector(1);
//		double reach = Math.min(i / 2 + 2, 8);
//		Vec3 end = start.add(lookAngle.x * reach, lookAngle.y * reach, lookAngle.z * reach);
//		ItemEntity dummy = DUMMY_ITEM_ENTITY.get();
//		if (dummy == null) {
//			DUMMY_ITEM_ENTITY.set(dummy = new ItemEntity(level, 0, 0, 0, ItemStack.EMPTY));
//		}
//		dummy.setLevel(level);
//		HitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, dummy));
//		dummy.setLevel(null);
//		end = hit.getLocation();
//		start = start.add(lookAngle);
////		level.addParticle(ParticleTypes.BUBBLE, start.x(), start.y(), start.z(), 0, 0, 0);
//		AABB aabb = new AABB(start, end).inflate(1);
//		extractContainer(level, gun, lookAngle, start, hit, aabb);
//		processEntities(level, player, gun, lookAngle, start, end, aabb);
//		if (i % 2 == 0) {
//			processBlocks(level, player, gun, lookAngle, start, end, aabb);
//		}
//	}
//
//	private static void extractContainer(Level level, ItemStack gun, Vec3 lookAngle, Vec3 start, HitResult hit, AABB aabb) {
//		if (hit.getType() != HitResult.Type.BLOCK) {
//			return;
//		}
//		String ammoType = getAmmoType(gun);
//		if (ammoType != null && !"item".equals(ammoType)) {
//			return;
//		}
//		int count = getItemCount(gun);
//		if (count >= MAX_ITEM_COUNT) {
//			return;
//		}
//		count += level.getEntitiesOfClass(
//				ItemEntity.class, aabb, e -> {
//					if (e.noPhysics || e.isSpectator() || !e.isAlive()) {
//						return false;
//					}
//					Vec3 center = e.getBoundingBox().getCenter();
//					return center.subtract(start).dot(lookAngle) > 0.866;
//				}).size();
//		if (count >= MAX_ITEM_COUNT) {
//			return;
//		}
//		BlockHitResult blockHitResult = (BlockHitResult) hit;
//		BlockPos pos = blockHitResult.getBlockPos();
//		BlockState state = level.getBlockState(pos);
//		BlockEntity blockEntity = level.getBlockEntity(pos);
//		Direction direction = blockHitResult.getDirection();
//		ItemStack itemStack = CommonProxy.extractOneItem(level, pos, state, blockEntity, direction);
//		if (itemStack.isEmpty()) {
//			return;
//		}
//		Vec3 end = hit.getLocation().add(direction.getStepX() * 0.2, direction.getStepY() * 0.2, direction.getStepZ() * 0.2);
//		ItemEntity itemEntity = new ItemEntity(level, end.x, end.y, end.z, itemStack);
//		itemEntity.setDeltaMovement(0, 0.1, 0);
//		level.addFreshEntity(itemEntity);
//	}
//
//	private static void processEntities(Level level, Player player, ItemStack gun, Vec3 lookAngle, Vec3 start, Vec3 end, AABB aabb) {
//		List<Entity> entities = level.getEntitiesOfClass(
//				Entity.class, aabb, e -> {
//					if (e == player || e.noPhysics || e.isSpectator() || !e.isAlive()) {
//						return false;
//					}
//					AABB box = e.getBoundingBox();
//					if (!e.getType().is(GadgetModule.VCD_MOVABLE)) {
//						if (box.getXsize() > 0.5 || box.getYsize() > 0.9 || box.getZsize() > 0.5) {
//							return false;
//						}
//					}
//					Vec3 center = box.getCenter();
//					return center.subtract(start).dot(lookAngle) > 0.866;
//				});
//		VacGunContainer container = null;
//		boolean save = false;
//		for (Entity entity : entities) {
//			if (entity.isPassenger()) {
//				Vec3 position = entity.position();
//				entity.stopRiding();
//				entity.setPos(position);
//			}
//			Vec3 center = entity.getBoundingBox().getCenter();
//			Vec3 dist = start.subtract(center);
//			double lengthSqr = dist.lengthSqr();
//			if (lengthSqr < 1 && entity instanceof ItemEntity itemEntity) {
//				if (container == null) {
//					container = readItemContainer(gun);
//				}
//				collectItem(player, itemEntity, gun, container);
//				save = true;
//				entity.hasImpulse = true;
//				continue;
//			}
//			Vec3 deltaMovement = entity.getDeltaMovement();
//			if (deltaMovement.y > LivingEntity.DEFAULT_BASE_GRAVITY) {
//				deltaMovement = deltaMovement.multiply(0.9, 0.9, 0.9);
//			}
//			Vec3 power = dist.normalize();
//			entity.setDeltaMovement(power);
//			entity.resetFallDistance();
////			entity.setDeltaMovement(deltaMovement);
////			if (lengthSqr < 4) {
////            }
//			entity.hasImpulse = true;
//			if (entity instanceof Mob mob) {
//				mob.getNavigation().stop();
//			}
//		}
//		if (save && container != null) {
//			saveItemContainer(gun, container);
//		}
//	}
//
//	private static void processBlocks(Level level, Player player, ItemStack gun, Vec3 lookAngle, Vec3 start, Vec3 end, AABB aabb) {
//		BlockPos.betweenClosedStream(aabb).forEach(pos -> {
//			Vec3 center = Vec3.atCenterOf(pos);
//			Vec3 dist = center.subtract(start);
//			if (dist.normalize().dot(lookAngle) < 0.866 && dist.lengthSqr() > 0.9) {
//				return;
//			}
//			BlockState state = level.getBlockState(pos);

	/// /			level.levelEvent(LevelEvent.PARTICLES_WAX_OFF, pos, 0);
//			if (state.isAir()) {
//				return;
//			}
//			if (CommonProxy.isLitCandle(state)) {
//				CommonProxy.extinguishCandle(player, state, level, pos);
//				return;
//			}
//			if (state.is(GadgetModule.VCD_PERFORM_USING)) {
//				BlockHitResult blockHitResult = new BlockHitResult(center, player.getDirection().getOpposite(), pos, false);
//				state.use(level, player, player.getUsedItemHand(), blockHitResult);
//				return;
//			}
//			if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
//				level.destroyBlock(pos, true, player);
//				return;
//			}
//			if (state.is(GadgetModule.VCD_PERFORM_BREAKING)) {
//				level.destroyBlock(pos, true, player);
//			}
//		});
//	}
//
//	@Override
//	public InteractionResult use(Level level, Player player, InteractionHand hand) {
//		return ItemUtils.startUsingInstantly(level, player, hand);
//	}
//
//	@Override
//	public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
//		return false;
//	}
//
//	@Override
//	public int getBarWidth(ItemStack itemStack) {
//		String ammoType = getAmmoType(itemStack);
//		if ("item".equals(ammoType)) {
//			int itemCount = getItemCount(itemStack);
//			return Mth.ceil((float) itemCount / MAX_ITEM_COUNT * 13);
//		} else if ("fluid".equals(ammoType)) {
//			// TODO
//		}
//		return 0;
//	}
//
	@Override
	public int getBarColor(ItemStack itemStack) {
		return ITEM_BAR_COLOR;
	}

	@Override
	public boolean isBarVisible(ItemStack itemStack) {
//		String ammoType = getAmmoType(itemStack);
//		if ("item".equals(ammoType)) {
//			return getItemCount(itemStack) > 0;
//		} else if ("fluid".equals(ammoType)) {
//			// TODO
//		}
		return false;
	}

	//	@Override
//	public boolean overrideOtherStackedOnMe(
//			ItemStack gun,
//			ItemStack itemStack,
//			Slot slot,
//			ClickAction clickAction,
//			Player player,
//			SlotAccess slotAccess) {
//		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player) || itemStack.isEmpty()) {
//			return false;
//		}
//		VacGunContainer container = readItemContainer(gun);
//		if (container == null) {
//			return false;
//		}
//		ItemStack itemStack2 = container.addItem(itemStack);
//		if (itemStack.getCount() == itemStack2.getCount()) {
//			return false;
//		}
//		itemStack.setCount(itemStack2.getCount());
//		saveItemContainer(gun, container);
//		return true;
//	}
//
//	@Override
//	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
//		super.appendHoverText(stack, worldIn, tooltip, flagIn);
//		String ammoType = getAmmoType(stack);
//		if ("item".equals(ammoType)) {
//			VacGunContainer container = readItemContainer(stack);
//			if (container != null) {
//				int i = 0;
//				int itemCount = 0;
//				for (ItemStack itemStack2 : container.items) {
//					if (itemStack2.isEmpty()) {
//						continue;
//					}
//					++i;
//					if (i > 4) {
//						break;
//					}
//					itemCount += itemStack2.getCount();
//					MutableComponent mutableComponent = itemStack2.getHoverName().copy();
//					mutableComponent.append(" x").append(String.valueOf(itemStack2.getCount()));
//					tooltip.add(mutableComponent);
//				}
//				if (container.getItemCount() > itemCount) {
//					tooltip.add(Component.translatable("container.shulkerBox.more", container.getItemCount() - itemCount)
//							.withStyle(ChatFormatting.ITALIC));
//				}
//			}
//		} else if ("fluid".equals(ammoType)) {
//			// TODO
//		}
//	}
	public static void playContainerAnimation(@Nullable BlockEntity blockEntity) {
		if (blockEntity instanceof Container && blockEntity instanceof MenuProvider menuProvider) {
			ContainerOpenerFakePlayer player = ContainerOpenerFakePlayer.getOrCreate(
					(ServerLevel) blockEntity.getLevel(), blockEntity.getBlockPos());
			player.openMenu(menuProvider);
		}
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return $ -> true;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 15;
	}

	@Override
	protected void shootProjectile(
			LivingEntity shooter,
			Projectile projectileEntity,
			int index,
			float power,
			float uncertainty,
			float angle,
			@Nullable LivingEntity targetOverrride) {

	}
}
