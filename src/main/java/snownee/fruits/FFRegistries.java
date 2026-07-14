package snownee.fruits;

import org.jspecify.annotations.Nullable;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import snownee.fruits.bee.BeeVariant;
import snownee.fruits.gadget.scent.ScentType;
import snownee.kiwi.Kiwi;

public class FFRegistries {

	public static void init() {
		Kiwi.registerInstantRegistry(FRUIT_TYPE_KEY);
		DynamicRegistries.registerSynced(BEE_VARIANT_KEY, BeeVariant.DIRECT_CODEC, BeeVariant.NETWORK_CODEC);
	}

	public static final ResourceKey<Registry<FruitType>> FRUIT_TYPE_KEY = ResourceKey.createRegistryKey(FruitfulFun.id("fruit_type"));
	public static final MappedRegistry<FruitType> FRUIT_TYPE = register(FRUIT_TYPE_KEY, FruitType.class, FruitfulFun.id("citron"));
	public static final ResourceKey<Registry<ScentType>> SCENT_TYPE_KEY = ResourceKey.createRegistryKey(FruitfulFun.id("scent_type"));
	public static final MappedRegistry<ScentType> SCENT_TYPE = register(SCENT_TYPE_KEY, ScentType.class, null);
	public static final ResourceKey<Registry<BeeVariant>> BEE_VARIANT_KEY = ResourceKey.createRegistryKey(FruitfulFun.id("bee_variant"));

	private static <T> MappedRegistry<T> register(ResourceKey<Registry<T>> registryKey, Class<?> clazz, @Nullable Identifier defaultId) {
		FabricRegistryBuilder<T, ? extends MappedRegistry<T>> builder;
		if (defaultId == null) {
			builder = FabricRegistryBuilder.create(registryKey);
		} else {
			builder = FabricRegistryBuilder.createDefaulted(registryKey, defaultId);
		}
		var registry = builder
				.attribute(RegistryAttribute.SYNCED)
				.buildAndRegister();
		Kiwi.registerRegistry(registryKey, clazz);
		return registry;
	}
}
