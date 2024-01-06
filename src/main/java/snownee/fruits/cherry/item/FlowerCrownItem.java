package snownee.fruits.cherry.item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFClientConfig;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.item.ModItem;

public class FlowerCrownItem extends ModItem implements Equipable {
	private final ParticleOptions particle;

	public FlowerCrownItem(Properties builder, ParticleOptions particle) {
		super(builder.stacksTo(1));
		this.particle = particle;
	}

	public static void spawnParticles(LivingEntity entity) {
		if (entity.isUnderWater() || entity.isInLava()) {
			return;
		}
		int i = 50;
		if (entity.isFallFlying() || entity.fallDistance > 3) {
			i = 3;
		} else if (entity.isSprinting()) {
			i = 10;
		}
		RandomSource random = entity.getRandom();
		if (random.nextInt(i) != 0) {
			return;
		}
		FlowerCrownItem item = CommonProxy.getFlowerCrown(entity);
		if (item == null) {
			return;
		}
		Vec3 eye = entity.getEyePosition();
		Vec3 lookAngle = entity.getLookAngle();
		double x = eye.x() + random.nextDouble() * 0.4 - 0.2 - lookAngle.x() * 0.2;
		double y = eye.y() + 0.2;
		double z = eye.z() + random.nextDouble() * 0.4 - 0.2 - lookAngle.z() * 0.2;
		Vec3 deltaMovement = entity.getDeltaMovement();
		double motionX = deltaMovement.x() - lookAngle.x() * 0.03;
		double motionY = deltaMovement.y() - lookAngle.y() * 0.03;
		double motionZ = deltaMovement.z() - lookAngle.z() * 0.03;
		entity.level().addParticle(item.getParticle(), x, y, z, motionX, motionY, motionZ);
	}

	@Override
	public @NotNull EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public @NotNull SoundEvent getEquipSound() {
		return CherryModule.EQUIP_CROWN.get();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		return swapWithEquipmentSlot(this, level, player, interactionHand);
	}

	public ParticleOptions getParticle() {
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Vanilla && CherryModule.CHERRY_CROWN.is(this)) {
			return ParticleTypes.CHERRY_LEAVES;
		}
		return particle;
	}
}
