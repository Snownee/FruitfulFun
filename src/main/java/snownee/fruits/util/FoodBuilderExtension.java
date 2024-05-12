package snownee.fruits.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import snownee.kiwi.util.CachedSupplier;

public class FoodBuilderExtension {
	private final FoodProperties.Builder builder;

	public FoodBuilderExtension(FoodProperties.Builder builder) {
		this.builder = builder;
	}

	public static FoodBuilderExtension of(FoodProperties.Builder builder) {
		return new FoodBuilderExtension(builder);
	}

	public FoodBuilderExtension effect(@Nullable Supplier<MobEffectInstance> mobEffectInstance, float probability) {
		if (mobEffectInstance != null) {
			builder.effect(new CachedSupplier<>(mobEffectInstance), probability);
		}
		return this;
	}

	public FoodProperties build() {
		return builder.build();
	}

	public FoodProperties.Builder builder() {
		return builder;
	}
}
