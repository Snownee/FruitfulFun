package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;

@Mixin(LivingEntity.class)
public class RideableBeeLivingEntityMixin {
	@Inject(method = "getRiddenInput", at = @At("HEAD"), cancellable = true)
	private void getRiddenInput(Player player, Vec3 vec3, CallbackInfoReturnable<Vec3> ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (Hooks.bee && entity instanceof Bee) {
			ci.setReturnValue(Hooks.getRiddenInput((Bee) entity, player, vec3));
		}
	}

	@Inject(method = "getRiddenSpeed", at = @At("HEAD"), cancellable = true)
	private void getRiddenSpeed(Player player, CallbackInfoReturnable<Float> ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (Hooks.bee && entity instanceof Bee) {
			float speed = (float) (
					entity.onGround() ?
							entity.getAttributeValue(Attributes.MOVEMENT_SPEED) :
							entity.getAttributeValue(Attributes.FLYING_SPEED));
			ci.setReturnValue(speed);
		}
	}

	@Inject(method = "tickRidden", at = @At("TAIL"), cancellable = true)
	private void tickRidden(Player player, Vec3 vec3, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (Hooks.bee && entity instanceof Bee) {
			Vec2 vec2 = this.getRiddenRotation(player);
			entity.setYRot(vec2.y % 360.0f);
			entity.setXRot(vec2.x % 360.0f);
			entity.yBodyRot = entity.yHeadRot = entity.getYRot();
			entity.yRotO = entity.yHeadRot;
			ci.cancel();
		}
	}

	@Inject(method = "dropEquipment", at = @At("HEAD"))
	private void dropEquipment(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Bee) {
			BeeAttributes attributes = BeeAttributes.of(entity);
			ItemStack saddle = attributes.getSaddle();
			if (!saddle.isEmpty()) {
				entity.spawnAtLocation(saddle);
				attributes.setSaddle(ItemStack.EMPTY);
			}
		}
	}

	@Unique
	private Vec2 getRiddenRotation(LivingEntity livingEntity) {
		return new Vec2(livingEntity.getXRot() * 0.5f, livingEntity.getYRot());
	}
}
