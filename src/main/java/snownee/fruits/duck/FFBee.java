package snownee.fruits.duck;

import snownee.fruits.bee.BeeAttributes;

public interface FFBee {
	BeeAttributes fruits$getBeeAttributes();

	void fruits$roll();

	void fruits$hornReturn(int ticks);

	boolean fruits$isHornReturnActive();
}
