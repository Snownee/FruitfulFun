package snownee.fruits.bee.genetics;

import net.minecraft.util.RandomSource;

public class Locus {
	private final Allele type;
	private byte data;

	public Locus(Allele type) {
		this.type = type;
		data = type.defaultData;
	}

	public void randomize(RandomSource random) {
		byte gene1 = type.randomize(random);
		byte gene2 = type.randomize(random);
		data = (byte) (gene1 << 4 | gene2);
	}

	public byte data() {
		return data;
	}

	public int high() {
		return (data & 0b11110000) >> 4;
	}

	public int low() {
		return data & 0b00001111;
	}

	public Allele type() {
		return type;
	}

	public void setData(byte b) {
		data = b;
		if (!type.allowedValues.contains(high()) || !type.allowedValues.contains(low())) {
			data = type.defaultData;
		}
	}
}
