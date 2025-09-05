package snownee.fruits.gadget;

import java.util.Locale;

import snownee.jade.api.SimpleStringRepresentable;

public enum BuzzyPowerType implements SimpleStringRepresentable {
	RED, GREEN, BLUE;

	private final String name = name().toLowerCase(Locale.ENGLISH);

	@Override
	public String toString() {
		return name;
	}
}
