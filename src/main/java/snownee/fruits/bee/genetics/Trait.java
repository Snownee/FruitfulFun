package snownee.fruits.bee.genetics;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record Trait(String name, int value) {
	public static final Map<String, Trait> REGISTRY = Maps.newLinkedHashMap();

	public static Collection<Trait> values() {
		return REGISTRY.values();
	}

	public static Trait register(Trait trait) {
		REGISTRY.put(trait.name, trait);
		return trait;
	}

	public static final Trait RAIN_CAPABLE = register(new Trait("rain_capable", 2));
	public static final Trait WITHER_TOLERANT = register(new Trait("wither_tolerant", 2));
	public static final Trait MOUNTABLE = register(new Trait("mountable", 6));
	public static final Trait FAST = register(new Trait("fast", 2));
	public static final Trait FASTER = register(new Trait("faster", 2));
	public static final Trait LAZY = register(new Trait("lazy", -2));
	public static final Trait MILD = register(new Trait("mild", 1));
	public static final Trait WARRIOR = register(new Trait("warrior", 2));
	public static final Trait ADVANCED_POLLINATION = register(new Trait("advanced_pollination", 4));
	public static final Trait PINK = register(new Trait("pink", 2));
	public static final Trait GHOST = register(new Trait("ghost", 0));

	public MutableComponent getDisplayName() {
		return Component.translatable("text.fruitfulfun.trait." + name);
	}

	public MutableComponent getDescription() {
		return Component.translatable("text.fruitfulfun.trait." + name + ".desc");
	}
}
