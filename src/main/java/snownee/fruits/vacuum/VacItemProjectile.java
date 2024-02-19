package snownee.fruits.vacuum;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.util.CommonProxy;
import snownee.fruits.vacuum.client.ItemProjectileColor;
import snownee.fruits.vacuum.client.ItemProjectileColors;

public class VacItemProjectile extends ThrowableItemProjectile {
	public final float bobOffs;
	private ItemProjectileColor colorProvider;

	public VacItemProjectile(EntityType<? extends VacItemProjectile> type, Level level) {
		super(type, level);
		this.bobOffs = this.random.nextFloat() * (float) Math.PI * 2.0f;
	}

	@Override
	protected Item getDefaultItem() {
		return Items.AIR;
	}

	@Override
	public ItemStack getItem() {
		return getItemRaw();
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		super.onSyncedDataUpdated(entityDataAccessor);
		if (level().isClientSide && DATA_ITEM_STACK.equals(entityDataAccessor)) {
			this.colorProvider = ItemProjectileColors.get(getItem());
		}
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if (!level().isClientSide && !getItem().isEmpty() && !isRemoved()) {
			Vec3 location = hitResult.getLocation();
			location = location.add(getDeltaMovement().normalize().scale(-0.25));
			ItemEntity itemEntity = new ItemEntity(level(), location.x, location.y, location.z, getItem());
			itemEntity.setPickUpDelay(4);
			if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getDirection().getAxis().isVertical()) {
				itemEntity.setDeltaMovement(getDeltaMovement().multiply(0.1, -0.1, 0.1));
			} else {
				itemEntity.setDeltaMovement((random.nextDouble() - 0.5) * 0.05, 0, (random.nextDouble() - 0.5) * 0.05);
			}
			Entity owner = getOwner();
			if (owner != null) {
				itemEntity.setThrower(owner.getUUID());
			}
			level().addFreshEntity(itemEntity);
			discard();
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		ItemStack item = getItem();
		if (!level().isClientSide && !item.isEmpty() && !isRemoved()) {
			BlockPos pos = blockHitResult.getBlockPos();
			BlockState blockState = level().getBlockState(pos);
			if (blockState.getBlock() instanceof FarmBlock) {
				Block block = Block.byItem(item.getItem());
				if (block != Blocks.AIR && block.defaultBlockState().is(BlockTags.CROPS)) {
					ItemStack itemCopy = item.copy();
					BlockHitResult newHit = new BlockHitResult(blockHitResult.getLocation(), Direction.UP, pos, false);
					// passing null player to make sure the item is consumed
					item.useOn(new UseOnContext(level(), null, InteractionHand.MAIN_HAND, item, newHit));
					updateItem();
					if (getOwner() instanceof ServerPlayer player) {
						CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, itemCopy);
						player.awardStat(Stats.ITEM_USED.get(itemCopy.getItem()));
					}
					return;
				}
			}
			BlockEntity blockEntity = level().getBlockEntity(pos);
			if (CommonProxy.insertItem(level(), pos, blockState, blockEntity, blockHitResult.getDirection(), item)) {
				playSound(SoundEvents.ITEM_PICKUP, 0.2f, ((random.nextFloat() - random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
				VacGunItem.playContainerAnimation(blockEntity);
				updateItem();
			}
		}
	}

	private void updateItem() {
		if (getItem().isEmpty()) {
			discard();
		} else {
			setItem(getItem());
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		DamageSource damageSource;
		Entity owner = getOwner();
		Entity entity = entityHitResult.getEntity();
		if (owner == null) {
			damageSource = damageSources().thrown(this, this);
		} else {
			damageSource = damageSources().thrown(this, owner);
			if (owner instanceof LivingEntity livingEntity) {
				livingEntity.setLastHurtMob(entity);
			}
		}
		boolean enderman = entity.getType() == EntityType.ENDERMAN;
		if (this.isOnFire() && !enderman) {
			entity.setSecondsOnFire(5);
		}
		if (!entity.hurt(damageSource, 1f) || enderman) {
			return;
		}
		if (entity instanceof LivingEntity livingEntity) {
			double dx = 0;
			double dz = 0;
			if (livingEntity.onGround()) {
				dx = getDeltaMovement().x * 0.05;
				dz = getDeltaMovement().z * 0.05;
			}
			livingEntity.push(dx, 0.1, dz);
			if (!level().isClientSide && owner instanceof LivingEntity) {
				EnchantmentHelper.doPostHurtEffects(livingEntity, owner);
				EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingEntity);
			}
			livingEntity.invulnerableTime = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (isRemoved()) {
			return;
		}
		if (level().isClientSide && colorProvider != null) {
			int color = colorProvider.getColor(getItem(), level(), position());
			Vec3 deltaMovement = getDeltaMovement();
			double x = deltaMovement.x;
			double y = deltaMovement.y;
			double z = deltaMovement.z;
			float l = Mth.sin(getAge() / 10.0f) * 0.1f + 0.2f;
			DustParticleOptions particleOptions = new DustParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1f);
			for (int i = 0; i < 4; ++i) {
				level().addParticle(
						particleOptions, getX() - x * (double) i / 4.0, getY() + l - y * (double) i / 4.0, getZ() - z * (double) i / 4.0,
						-x, -y + 0.2, -z);
			}
		}
	}

	public int getAge() {
		return tickCount;
	}

	public float getSpin(float f) {
		return ((float) this.getAge() + f) / 20.0f + this.bobOffs;
	}

	@Override
	public Component getName() {
		Component component = this.getCustomName();
		if (component != null) {
			return component;
		}
		return Component.translatable(this.getItem().getDescriptionId());
	}

	@Nullable
	@Override
	public ItemStack getPickResult() {
		return getItem();
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		if (entity instanceof VacItemProjectile) {
			return false;
		}
		return super.canCollideWith(entity);
	}
}
