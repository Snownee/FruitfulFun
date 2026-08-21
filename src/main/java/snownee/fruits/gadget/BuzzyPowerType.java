package snownee.fruits.gadget;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum BuzzyPowerType implements StringRepresentable {
	RED, GREEN, BLUE;

	private final String name = name().toLowerCase(Locale.ENGLISH);

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
