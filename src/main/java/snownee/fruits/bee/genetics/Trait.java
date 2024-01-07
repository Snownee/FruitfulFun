package snownee.fruits.bee.genetics;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record Trait(String name) {
	public static final Map<String, Trait> REGISTRY = Maps.newLinkedHashMap();

	public static Collection<Trait> values() {
		return REGISTRY.values();
	}

	public static Trait register(Trait trait) {
		REGISTRY.put(trait.name, trait);
		return trait;
	}

	public static final Trait RAIN_CAPABLE = register(new Trait("rain_capable"));
	public static final Trait WITHER_TOLERANT = register(new Trait("wither_tolerant"));
	public static final Trait MOUNTABLE = register(new Trait("mountable"));
	public static final Trait FAST = register(new Trait("fast"));
	public static final Trait FASTER = register(new Trait("faster"));
	public static final Trait LAZY = register(new Trait("lazy"));
	public static final Trait MILD = register(new Trait("mild"));
	public static final Trait WARRIOR = register(new Trait("warrior"));
	public static final Trait ADVANCED_POLLINATION = register(new Trait("advanced_pollination"));
	public static final Trait PINK = register(new Trait("pink"));

	public MutableComponent getDisplayName() {
		return Component.translatable("text.fruitfulfun.trait." + name);
	}

	public MutableComponent getDescription() {
		return Component.translatable("text.fruitfulfun.trait." + name + ".desc");
	}

}
