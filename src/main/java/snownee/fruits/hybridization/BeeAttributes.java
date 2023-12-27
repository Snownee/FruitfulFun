package snownee.fruits.hybridization;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Bee;

public class BeeAttributes {
	private final List<String> pollens = Lists.newArrayList();

	public static BeeAttributes of(Bee bee) {
		return ((FFBee) bee).fruits$getBeeAttributes();
	}

	public void write(CompoundTag data) {
	}

	public void read(CompoundTag data) {
	}

	public List<String> getPollens() {
		return pollens;
	}
}
