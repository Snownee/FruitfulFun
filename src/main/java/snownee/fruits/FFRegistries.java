package snownee.fruits;

import java.util.function.Consumer;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import snownee.kiwi.Kiwi;

public class FFRegistries {
	public static DefaultedRegistry<FruitType> FRUIT_TYPE;

	public static void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((NewRegistryEvent event) -> init(event));
	}

	public static void init(NewRegistryEvent event) {
		FFRegistries.<FruitType>register("fruit_type", FruitType.class, "citron", event, v -> FRUIT_TYPE = v);
	}

	private static <T> void register(
			String name,
			Class<?> clazz,
			String defaultKey,
			NewRegistryEvent event,
			Consumer<DefaultedRegistry<T>> consumer) {
		RegistryBuilder<T> builder = new RegistryBuilder<T>().setName(new ResourceLocation(FruitfulFun.ID, name))
				.setDefaultKey(new ResourceLocation(FruitfulFun.ID, defaultKey))
				.hasTags(); // call hasWrapper()
		event.create(builder, v -> {
			Registry<?> registry = BuiltInRegistries.REGISTRY.get(v.getRegistryKey().location());
			//noinspection unchecked
			consumer.accept((DefaultedRegistry<T>) registry);
			Kiwi.registerRegistry(v, clazz);
		});
	}
}
