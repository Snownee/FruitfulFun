package snownee.fruits.util;

import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class FoodBuilderExtension {
	private final FoodProperties.Builder builder;

	public FoodBuilderExtension(FoodProperties.Builder builder) {
		this.builder = builder;
	}

	public static FoodBuilderExtension of(FoodProperties.Builder builder) {
		return new FoodBuilderExtension(builder);
	}

	public FoodBuilderExtension effect(Supplier<MobEffectInstance> mobEffectInstance, float probability) {
		return effect(new CachedSupplier<>(mobEffectInstance), probability);
	}

	public FoodBuilderExtension effect(CachedSupplier<MobEffectInstance> mobEffectInstance, float probability) {
		builder.effects.add(new LazyEffect(mobEffectInstance, probability));
		return this;
	}

	public FoodProperties build() {
		return builder.build();
	}

	public FoodProperties.Builder builder() {
		return builder;
	}

	private static class LazyEffect extends Pair<MobEffectInstance, Float> {
		private final CachedSupplier<MobEffectInstance> effectSupplier;

		public LazyEffect(Supplier<MobEffectInstance> first, Float second) {
			super(null, second); // we assume people never hash this object...
			this.effectSupplier = first instanceof CachedSupplier<MobEffectInstance> $ ? $ : new CachedSupplier<>(first);
		}

		@Override
		public MobEffectInstance getFirst() {
			return effectSupplier.get();
		}
	}
}
