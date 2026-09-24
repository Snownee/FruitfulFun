package snownee.fruits.mixin.cherry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.cherry.item.FlowerCrownItem;

@Mixin(EntityEquipment.class)
public class FlowerCrownParticlesMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(Entity entity, CallbackInfo ci) {
		if (!entity.level().isClientSide() || !(entity instanceof LivingEntity livingEntity)) {
			return;
		}
		ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.getItem() instanceof FlowerCrownItem) {
			FlowerCrownItem.spawnParticles(livingEntity);
		}
	}
}