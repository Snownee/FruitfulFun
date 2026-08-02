package snownee.fruits.gadget;

import java.util.List;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.gadget.network.SScentAddedPacket;
import snownee.fruits.util.CommonProxy;

public class ScentType {
	private final List<MobEffectInstance> effects;
	private final int color;
	private final float rate;

	public ScentType(List<MobEffectInstance> effects) {
		this(effects, 1.0f);
	}

	public ScentType(List<MobEffectInstance> effects, float rate) {
		this(effects, effects.isEmpty() ? -1 : effects.get(0).getEffect().getColor(), rate);
	}

	public ScentType(List<MobEffectInstance> effects, int color, float rate) {
		this.effects = effects;
		this.color = color;
		this.rate = rate;
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
				SScentAddedPacket.send(entity, effect.getEffect().getColor());
			}
		}
	}

	public List<MobEffectInstance> effects() {
		return effects;
	}

	public int color() {
		return color;
	}

	public float rate() {
		return rate;
	}
}
