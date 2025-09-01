package snownee.fruits;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import snownee.fruits.gadget.ScentType;
import snownee.kiwi.Kiwi;

public class FFRegistries {
	public static void init() {
	}

	public static final MappedRegistry<FruitType> FRUIT_TYPE = register("fruit_type", FruitType.class, FruitfulFun.id("citron"));
	public static final MappedRegistry<ScentType> SCENT_TYPE = register("scent_type", ScentType.class, null);

	private static <T> MappedRegistry<T> register(String name, Class<?> clazz, @Nullable ResourceLocation defaultId) {
		FabricRegistryBuilder<T, ? extends MappedRegistry<T>> builder;
		if (defaultId == null) {
			builder = FabricRegistryBuilder.createSimple(ResourceKey.createRegistryKey(FruitfulFun.id(name)));
		} else {
			builder = FabricRegistryBuilder.createDefaulted(ResourceKey.createRegistryKey(FruitfulFun.id(name)), defaultId);
		}
		var registry = builder
				.attribute(RegistryAttribute.SYNCED)
				.buildAndRegister();
		Kiwi.registerRegistry(registry, clazz);
		return registry;
	}
}
