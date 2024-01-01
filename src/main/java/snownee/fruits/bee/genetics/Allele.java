package snownee.fruits.bee.genetics;

import java.util.Collection;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import snownee.fruits.FFCommonConfig;

public class Allele {
	public static final Map<String, Allele> REGISTRY = Maps.newLinkedHashMap();

	public static Collection<Allele> values() {
		return REGISTRY.values();
	}

	public static Allele register(Allele type) {
		REGISTRY.put(type.name, type);
		return type;
	}

	public static final Allele RAINC = register(new Allele("RC", 2, FFCommonConfig.mutationRateRC));
	public static final Allele FANCY = register(new Allele("FC", 3, FFCommonConfig.mutationRateFC));
	public static final Allele FEAT1 = register(new Allele("FT1", 3, FFCommonConfig.mutationRateFT1));
	public static final Allele FEAT2 = register(new Allele("FT2", 3, FFCommonConfig.mutationRateFT2));

	public final String name;
	public final int defaultValue;
	public final byte defaultData;
	public final IntList allowedValues;
	public final float mutationRate;

	public Allele(String name, int allowedValues, float mutationRate) {
		this.name = name;
		this.mutationRate = mutationRate;
		this.defaultValue = 0;
		this.defaultData = 0;
		this.allowedValues = IntImmutableList.toList(IntStream.range(0, allowedValues));
	}

	public byte maybeMutate(byte data, RandomSource random) {
		if (mutationRate > 0 && random.nextFloat() < mutationRate) {
			return (byte) allowedValues.getInt(random.nextInt(allowedValues.size()));
		}
		return data;
	}

	public byte randomize(RandomSource random) {
		return maybeMutate((byte) defaultValue, random);
	}

	public Component getDisplayName(int data) {
		return Component.literal(name + (data + 1));
	}
}
