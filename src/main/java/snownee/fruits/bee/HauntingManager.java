package snownee.fruits.bee;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

public class HauntingManager {
	public final Entity target;
	private int fireCounter;
	private long lastDamage;

	public HauntingManager(Entity target) {
		this.target = target;
	}

	public void hurtInFire(ServerPlayer player) {
		if (player.level().getGameTime() - lastDamage < 30) {
			if (++fireCounter >= 4) {
				getExorcised(player);
			}
		} else {
			fireCounter = 0;
		}
		lastDamage = player.level().getGameTime();
	}

	public void getExorcised(ServerPlayer player) {
		player.setCamera(null);
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 1));
		player.addEffect(new MobEffectInstance(BeeModule.FRAGILITY.get(), 400, 1));
	}
}
