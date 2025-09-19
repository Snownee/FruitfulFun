package snownee.fruits;

import java.util.function.Consumer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import snownee.fruits.gadget.ScentType;
import snownee.kiwi.Kiwi;

public class FFRegistries {
	public static Registry<FruitType> FRUIT_TYPE;
	public static Registry<ScentType> SCENT_TYPE;

	public static void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((NewRegistryEvent event) -> init(event));
	}

	public static void init(NewRegistryEvent event) {
		FFRegistries.<FruitType>register("fruit_type", FruitType.class, "citron", event, v -> FRUIT_TYPE = v);
		FFRegistries.<ScentType>register("scent_type", ScentType.class, null, event, v -> SCENT_TYPE = v);
	}

	private static <T> void register(
			String name,
			Class<?> clazz,
			String defaultKey,
			NewRegistryEvent event,
			Consumer<Registry<T>> consumer) {
		RegistryBuilder<T> builder = new RegistryBuilder<T>().setName(FruitfulFun.id(name)).hasTags();
		if (defaultKey != null) {
			builder.setDefaultKey(FruitfulFun.id(defaultKey));
		}
		event.create(
				builder, v -> {
					Registry<?> registry = BuiltInRegistries.REGISTRY.get(v.getRegistryKey().location());
					//noinspection unchecked
					consumer.accept((Registry<T>) registry);
					Kiwi.registerRegistry(v, clazz);
				});
	}
}
