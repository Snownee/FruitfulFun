package snownee.fruits.bee.genetics;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import snownee.fruits.bee.BeeVariant;
import snownee.fruits.bee.BeeVariants;

public record Trait(String name, int value, Optional<ResourceKey<BeeVariant>> variant) {
	public static final Map<String, Trait> REGISTRY = Maps.newLinkedHashMap();
	public static final Codec<Trait> CODEC = ExtraCodecs.idResolverCodec(Codec.STRING, REGISTRY::get, Trait::name);
	public static final StreamCodec<ByteBuf, Trait> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(REGISTRY::get, trait -> trait.name);

	public Trait(String name, int value) {
		this(name, value, Optional.empty());
	}

	public Trait(String name, int value, ResourceKey<BeeVariant> variant) {
		this(name, value, Optional.of(variant));
	}

	public static Collection<Trait> values() {
		return REGISTRY.values();
	}

	public static Trait register(Trait trait) {
		REGISTRY.put(trait.name, trait);
		return trait;
	}

	public static final Trait RAIN_CAPABLE = register(new Trait("rain_capable", 2));
	public static final Trait WITHER_TOLERANT = register(new Trait("wither_tolerant", 2, BeeVariants.WITHER));
	public static final Trait MOUNTABLE = register(new Trait("mountable", 6));
	public static final Trait FAST = register(new Trait("fast", 2));
	public static final Trait FASTER = register(new Trait("faster", 2));
	public static final Trait LAZY = register(new Trait("lazy", -2));
	public static final Trait MILD = register(new Trait("mild", 1));
	public static final Trait WARRIOR = register(new Trait("warrior", 2));
	public static final Trait ADVANCED_POLLINATION = register(new Trait("advanced_pollination", 4));
	public static final Trait PINK = register(new Trait("pink", 2, BeeVariants.PINK));
	public static final Trait GHOST = register(new Trait("ghost", 0, BeeVariants.GHOST));

	public MutableComponent displayName() {
		return Component.translatable("text.fruitfulfun.trait." + name);
	}

	public MutableComponent description() {
		return Component.translatable("text.fruitfulfun.trait." + name + ".desc");
	}
}
