package snownee.fruits.gadget;

import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class BuzzyShieldItem extends ShieldItem implements BuzzyItemCategoryFiller {
	public static final String TAG_LAST_PERFECT_BLOCK = "last_perfect_block";

	public BuzzyShieldItem(Properties properties) {
		super(properties);
	}

	public static float onBlock(LivingEntity self, DamageSource source, float damage, ItemStack shield) {
		BuzzyPowerStorage storage = getPowerStorage(shield);
		if (shield.getTag() == null || !shield.getTag().getBoolean("Unbreakable")) {
			storage.useLife(200); // durability is 120000 / 200 = 600
			BuzzyPowerStorage.write(shield, storage);
		}
		int ticksUsingItem = self.getTicksUsingItem();
		if (ticksUsingItem > 0 && ticksUsingItem <= 6) {
			shield.getOrCreateTag().putLong(TAG_LAST_PERFECT_BLOCK, self.level().getGameTime());
			Set<LivingEntity> entities = Sets.newLinkedHashSet();
			Level level = self.level();
			entities.addAll(level.getEntitiesOfClass(LivingEntity.class, self.getBoundingBox().inflate(1)));
			Vec3 viewVector = self.getViewVector(0);
			entities.addAll(level.getEntitiesOfClass(LivingEntity.class, self.getBoundingBox().inflate(1.5).move(viewVector.scale(1.5))));
			for (LivingEntity entity : entities) {
				if (entity == self || entity.isAlliedTo(self) || !entity.attackable()) {
					continue;
				}
				entity.knockback(1f, self.getX() - entity.getX(), self.getZ() - entity.getZ());
			}
			if (source.getEntity() instanceof LivingEntity target && target.canBeSeenAsEnemy()) {
				summonBees(self, target);
			}
			return 0;
		}
		return damage * 0.5f;
	}

	public static void summonBees(LivingEntity entity, @Nullable LivingEntity target) {
		Level level = entity.level();
		Vec3 viewVector = entity.getViewVector(0);
		Vec3 basePos = entity.getEyePosition().add(viewVector.scale(1.5));
		for (int i = 0; i < 3; i++) {
			SummonedBee bee = new SummonedBee(GadgetModule.SUMMONED_BEE.get(), level);
			Vec3 b1, b2;
			if (viewVector.z < -0.9999999) {
				b1 = new Vec3(0, -1, 0);
				b2 = new Vec3(-1, 0, 0);
			} else {
				double a = 1 / (1 + viewVector.z);
				double b = -a * viewVector.x * viewVector.y;
				b1 = new Vec3(1 - a * viewVector.x * viewVector.x, b, -viewVector.x);
				b2 = new Vec3(b, 1 - a * viewVector.y * viewVector.y, -viewVector.y);
			}
			for (int attempts = 0; attempts < 10; attempts++) {
				double angle = entity.random.nextDouble() * Mth.TWO_PI;
				Vec3 normal = b1.scale(Math.cos(angle)).add(b2.scale(Math.sin(angle)));
				bee.setPos(basePos.x + normal.x * 1.5, basePos.y + normal.y * 1.5, basePos.z + normal.z * 1.5);
				if (!level.noCollision(bee)) {
					continue;
				}
				BlockPathTypes pathType = bee.getNavigation().getNodeEvaluator().getBlockPathType(
						level,
						bee.getBlockX(),
						bee.getBlockY(),
						bee.getBlockZ());
				if (bee.getPathfindingMalus(pathType) != 0) {
					continue;
				}
				bee.setOwner(entity);
				bee.setXRot(entity.getXRot());
				bee.setYRot(entity.getYRot());
				if (i == 0 && target != null) {
					bee.setAttackTarget(target);
				}
				level.addFreshEntity(bee);
				break;
			}
		}
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return false;
	}

	public static ItemStack getItemInHand(LivingEntity entity) {
		if (entity.getMainHandItem().getItem() instanceof BuzzyShieldItem item && !item.hasNoPower(entity.getMainHandItem())) {
			return entity.getMainHandItem();
		}
		if (entity.getOffhandItem().getItem() instanceof BuzzyShieldItem item && !item.hasNoPower(entity.getOffhandItem())) {
			return entity.getOffhandItem();
		}
		return ItemStack.EMPTY;
	}

	public static BuzzyPowerStorage getPowerStorage(ItemStack itemStack) {
		return BuzzyPowerStorage.read(itemStack).orElseGet(() -> new BuzzyPowerStorage(120000f));
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
		return hasNoPower(itemStack) ? ItemUseAnimation.NONE : super.getUseAnimation(itemStack);
	}

	public boolean hasNoPower(ItemStack itemStack) {
		return BuzzyPowerStorage.read(itemStack).map(BuzzyPowerStorage::isEmpty).orElse(true);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack itemStack = player.getItemInHand(usedHand);
		if (!level.isClientSide() && player.isCreative() && player.getOffhandItem().is(Items.DEBUG_STICK)) {
			summonBees(player, null);
		}
		if (hasNoPower(itemStack)) {
			if (!level.isClientSide()) {
				player.displayClientMessage(Component.translatable("tip.fruitfulfun.notEnoughPower"), true);
			}
			return InteractionResultHolder.fail(itemStack);
		}
		return super.use(level, player, usedHand);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BuzzyPowerStorage.isBarVisible(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BuzzyPowerStorage.getBarColor(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return BuzzyPowerStorage.getBarWidth(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("tip.fruitfulfun.whenInHand").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("tip.fruitfulfun.halveAttackCd").withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.translatable("tip.fruitfulfun.whenPerfectBlock").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("tip.fruitfulfun.summonBees").withStyle(ChatFormatting.BLUE));
	}
}
