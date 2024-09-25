package snownee.fruits;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import snownee.kiwi.Kiwi;

public class FFRegistries {
	public static final DefaultedMappedRegistry<FruitType> FRUIT_TYPE = register(
			"fruit_type", FruitType.class, FruitfulFun.id("citron"));

	public static void init() {
	}

	private static <T> DefaultedMappedRegistry<T> register(String name, Class<?> clazz, ResourceLocation defaultId) {
		var registry = FabricRegistryBuilder.<T>createDefaulted(
						ResourceKey.createRegistryKey(FruitfulFun.id(name)), defaultId)
				.attribute(RegistryAttribute.SYNCED)
				.buildAndRegister();
		Kiwi.registerRegistry(registry, clazz);
		return registry;
	}
}
