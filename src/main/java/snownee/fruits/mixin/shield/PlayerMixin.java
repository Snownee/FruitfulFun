package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.BuzzyShieldItem;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@WrapMethod(method = "getCurrentItemAttackStrengthDelay")
	private float getCurrentItemAttackStrengthDelay(Operation<Float> original) {
		return !Hooks.gadget || BuzzyShieldItem.getItemInHand(this).isEmpty() ? original.call() : original.call() * 0.5f;
	}
}
