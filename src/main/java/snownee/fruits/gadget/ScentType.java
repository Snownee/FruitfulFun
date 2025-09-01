package snownee.fruits.gadget;

import java.util.List;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.util.CommonProxy;

public class ScentType {
	private final List<MobEffectInstance> effects;

	public ScentType(List<MobEffectInstance> effects) {
		this.effects = effects;
	}

	public boolean isActiveAt(LevelChunk chunk) {
		return getTime(chunk) > chunk.getLevel().getGameTime();
	}

	public long getTime(ChunkAccess chunk) {
		return CommonProxy.getScentTime(chunk, this);
	}

	public void setTime(ChunkAccess chunk, long time) {
		CommonProxy.setScentTime(chunk, this, time);
	}

	public void tick(LivingEntity entity, LevelChunk chunk) {
		for (MobEffectInstance effect : effects) {
			if (!entity.canBeAffected(effect)) {
				continue;
			}
			MobEffectInstance existingEffect = entity.getEffect(effect.getEffect());
			boolean hasEffect = existingEffect != null;
			if (hasEffect && entity.tickCount % 40 != 0) {
				continue;
			}
			int duration = hasEffect ? existingEffect.getDuration() : 0;
			duration = Math.min(effect.getDuration(), duration + 60);
			effect = new MobEffectInstance(effect);
			effect.duration = duration;
			if (entity.addEffect(effect) && !hasEffect && !effect.isVisible()) {
				entity.level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, entity.blockPosition(), effect.getEffect().getColor());
			}
		}
	}
}
