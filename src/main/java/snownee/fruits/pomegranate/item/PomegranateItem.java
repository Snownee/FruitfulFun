package snownee.fruits.pomegranate.item;

import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.item.ModBlockItem;

public class PomegranateItem extends ModBlockItem {

	public PomegranateItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer() == null || context.isSecondaryUseActive()) {
			return super.useOn(context);
		}
		BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
		if (PomegranateModule.POMEGRANATE_LEAVES.is(blockState)) {
			return super.useOn(context);
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		player.getCooldowns().addCooldown(itemStack.getItem(), 10);
		if (!level.isClientSide) {
			Vec3 eye = player.getEyePosition();
			ItemEntity fruit = new ItemEntity(level, eye.x, eye.y, eye.z, itemStack.copyWithCount(1));
			fruit.setThrower(player.getUUID());
			fruit.setPickUpDelay(5 * 20);
			shootFromRotation(player, fruit, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 0.5f);
			level.addFreshEntity(fruit);
		}
		player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
		if (!player.getAbilities().instabuild) {
			itemStack.shrink(1);
		}
		return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
	}

	private static void shootFromRotation(Entity thrower, Entity fruit, float f, float g, float h, float i, float j) {
		float k = -Mth.sin(g * ((float) Math.PI / 180)) * Mth.cos(f * ((float) Math.PI / 180));
		float l = -Mth.sin((f + h) * ((float) Math.PI / 180));
		float m = Mth.cos(g * ((float) Math.PI / 180)) * Mth.cos(f * ((float) Math.PI / 180));
		shoot(fruit, k, l, m, i, j);
		Vec3 vec3 = thrower.getDeltaMovement();
		fruit.setDeltaMovement(fruit.getDeltaMovement().add(vec3.x, thrower.onGround() ? 0.0 : vec3.y, vec3.z));
	}

	private static void shoot(Entity fruit, double d, double e, double f, float g, float h) {
		Vec3 vec3 = new Vec3(d, e, f).normalize().add(
				fruit.random.triangle(0.0, 0.0172275 * (double) h),
				fruit.random.triangle(0.0, 0.0172275 * (double) h),
				fruit.random.triangle(0.0, 0.0172275 * (double) h)).scale(g);
		fruit.setDeltaMovement(vec3);
		double i = vec3.horizontalDistance();
		fruit.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
		fruit.setXRot((float) (Mth.atan2(vec3.y, i) * 57.2957763671875));
		fruit.yRotO = fruit.getYRot();
		fruit.xRotO = fruit.getXRot();
	}
}
