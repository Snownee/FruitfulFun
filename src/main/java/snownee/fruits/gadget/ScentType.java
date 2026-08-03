package snownee.fruits.gadget;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.gadget.network.SScentAddedPacket;
import snownee.fruits.util.CommonProxy;

public class ScentType {
	private final Supplier<List<MobEffectInstance>> effects;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private OptionalInt color;
	private final float rate;

	public ScentType(Supplier<List<MobEffectInstance>> effects) {
		this(effects, 1.0f);
	}

	public ScentType(Supplier<List<MobEffectInstance>> effects, float rate) {
		this(effects, -1, rate);
	}

	public ScentType(Supplier<List<MobEffectInstance>> effects, int color, float rate) {
		this.effects = Suppliers.memoize(effects::get);
		this.color = color == -1 ? OptionalInt.empty() : OptionalInt.of(color);
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
		for (MobEffectInstance effect : effects.get()) {
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
		return effects.get();
	}

	public int color() {
		if (color.isEmpty() && !effects().isEmpty()) {
			this.color = OptionalInt.of(effects().get(0).getEffect().getColor());
		}
		return color.orElse(-1);
	}

	public float rate() {
		return rate;
	}
}
