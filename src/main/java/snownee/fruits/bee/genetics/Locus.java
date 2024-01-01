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

	public byte getData() {
		return data;
	}

	public int getHigh() {
		return (data & 0b11110000) >> 4;
	}

	public int getLow() {
		return data & 0b00001111;
	}

	public Allele getType() {
		return type;
	}

	public void setData(byte b) {
		data = b;
		if (!type.allowedValues.contains(getHigh()) || !type.allowedValues.contains(getLow())) {
			data = type.defaultData;
		}
	}
}
