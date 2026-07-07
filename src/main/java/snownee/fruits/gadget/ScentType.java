package snownee.fruits.gadget;

import java.util.List;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.gadget.network.SScentAddedPacket;
import snownee.fruits.util.CommonProxy;

public class ScentType {
	private final List<MobEffectInstance> effects;
	private final int color;

	public ScentType(List<MobEffectInstance> effects) {
		this(effects, effects.isEmpty() ? -1 : effects.getFirst().getEffect().value().getColor());
	}

	public ScentType(List<MobEffectInstance> effects, int color) {
		this.effects = effects;
		this.color = color;
	}

	public boolean isActiveAt(LevelChunk chunk) {
		return isActiveAt(chunk.getLevel(), chunk);
	}

	public boolean isActiveAt(LevelAccessor level, ChunkAccess chunk) {
		return getTime(chunk) > level.getGameTime();
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
				SScentAddedPacket.send(entity, effect.getEffect().value().getColor());
			}
		}
	}

	public List<MobEffectInstance> effects() {
		return effects;
	}

	public int color() {
		return color;
	}
}
