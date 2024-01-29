package snownee.fruits.util;

import java.util.function.Supplier;

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
		builder.effect(new CachedSupplier<>(mobEffectInstance), probability);
		return this;
	}

	public FoodProperties build() {
		return builder.build();
	}

	public FoodProperties.Builder builder() {
		return builder;
	}
}
