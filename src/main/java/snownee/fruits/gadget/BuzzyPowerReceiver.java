package snownee.fruits.gadget;

import org.jetbrains.annotations.Nullable;

public interface BuzzyPowerReceiver {
	float addPower(BuzzyPowerType type, float amount);

	@Nullable BuzzyPowerStorage view();
}
