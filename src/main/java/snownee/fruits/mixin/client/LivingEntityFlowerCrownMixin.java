package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.cherry.item.FlowerCrownItem;

@Mixin(value = LivingEntity.class, priority = 500)
public class LivingEntityFlowerCrownMixin {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updatingUsingItem()V"))
	private void tick(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!entity.level().isClientSide) {
			return;
		}
		if (entity.getType() == EntityType.PLAYER || entity.getType() == EntityType.ARMOR_STAND) {
			FlowerCrownItem.spawnParticles(entity);
		}
	}
}
