package snownee.fruits.bee.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.mixin.LivingEntityAccess;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket("haunting_action")
public class CHauntingActionPacket extends PacketHandler {
	public static CHauntingActionPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		return executor.apply(() -> {
			if (Objects.requireNonNull(player).hasEffect(MobEffects.WEAKNESS) || !BeeModule.isHauntingNormalEntity(player, null)) {
				return;
			}
			HauntingManager manager = ((FFPlayer) player).fruits$hauntingManager();
			if (manager == null || !(((FFPlayer) player).fruits$hauntingTarget() instanceof LivingEntity target)) {
				return;
			}
			boolean success = false;
			if (manager.hasTrait(Trait.LAZY)) {
				MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 3);
				target.addEffect(effectInstance);
				player.addEffect(new MobEffectInstance(effectInstance));
				success = true;
			}
			if (manager.hasTrait(Trait.FASTER)) {
				MobEffectInstance effect = target.getEffect(MobEffects.JUMP);
				if (effect == null || effect.getAmplifier() == 0) {
					target.forceAddEffect(new MobEffectInstance(MobEffects.JUMP, 100, 1, false, false, false), player);
				}
				((LivingEntityAccess) target).callJumpFromGround();
				if (effect == null) {
					target.removeEffect(MobEffects.JUMP);
				} else {
					target.forceAddEffect(effect, null);
				}
				success = true;
			} else if (manager.hasTrait(Trait.FAST)) {
				((LivingEntityAccess) target).callJumpFromGround();
				success = true;
			}
			if (success) {
				player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 90, 0, false, false, true));
			}
		});
	}
}
