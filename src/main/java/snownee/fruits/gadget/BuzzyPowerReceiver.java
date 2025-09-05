package snownee.fruits.gadget;

@FunctionalInterface
public interface BuzzyPowerReceiver {
	float addPower(BuzzyPowerType type, float amount);
}
