package snownee.fruits.gadget;

import org.jspecify.annotations.Nullable;

public interface BuzzyPowerReceiver {
	float addPower(BuzzyPowerType type, float amount);

	@Nullable BuzzyPowerStorage view();
}
