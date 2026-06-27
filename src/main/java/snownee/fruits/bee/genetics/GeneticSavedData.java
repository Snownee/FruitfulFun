package snownee.fruits.bee.genetics;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import snownee.fruits.FruitfulFun;

public class GeneticSavedData extends SavedData {
	private static final Codec<GeneticSavedData> CODEC = AlleleRecord.CODEC.listOf().fieldOf("alleles").codec().xmap(
			list -> {
				GeneticSavedData data = new GeneticSavedData();
				for (AlleleRecord record : list) {
					data.alleles.put(record.code, record);
				}
				return data;
			},
			data -> data.alleles.values().stream().toList()
	);
	public static final SavedDataType<GeneticSavedData> TYPE = new SavedDataType<>(
			FruitfulFun.id("genetics"), GeneticSavedData::new, CODEC, null
	);
	private final Map<String, AlleleRecord> alleles = Maps.newHashMap();

	public void initAlleles(long seed) {
		Collection<Allele> values = Allele.values();
		for (Allele allele : values) {
			allele.codename = "0";
			allele.index = -1;
		}
		RandomSource random = RandomSource.create(seed);
		float[] hues = pickEvenlySpacedPoints(values.size(), 0.6F / values.size(), random);
		int i = 0;
		for (Allele allele : values) {
			AlleleRecord alleleRecord = alleles.get(allele.name);
			if (alleleRecord != null) {
				allele.codename = alleleRecord.code;
				allele.index = alleleRecord.index;
				random.nextInt(26);
				random.nextInt(255);
			} else {
				int codename = random.nextInt(26);
				while (Allele.byCode(String.valueOf((char) ('A' + codename))) != null) {
					codename = (codename + 1) % 26;
				}
				allele.codename = String.valueOf((char) ('A' + codename));
				int index = random.nextInt(255);
				while (Allele.byIndex(index) != null) {
					index = (index + 31) % 255;
				}
				allele.index = index;
				alleles.put(allele.name, new AlleleRecord(allele.codename, allele.index));
				setDirty();
			}
			allele.color = Mth.hsvToRgb(hues[i++], 0.86f, 0.86f);
		}
		Allele.BY_CODE = values.stream().sorted(Comparator.comparing(a -> a.codename)).toList();
	}

	private static float[] pickEvenlySpacedPoints(int count, float minDistance, RandomSource random) {
		float freeSpace = 1 - count * minDistance;
		if (freeSpace < 0) {
			throw new IllegalArgumentException("Cannot place " + count + " points with min distance " + minDistance);
		}
		float[] cuts = new float[count];
		for (int i = 0; i < count; i++) {
			cuts[i] = random.nextFloat() * freeSpace;
		}
		Arrays.sort(cuts);
		float[] gaps = new float[count - 1];
		gaps[0] = cuts[0];
		for (int i = 1; i < count - 1; i++) {
			gaps[i] = cuts[i] - cuts[i - 1];
		}
		for (int i = 0; i < count - 1; i++) {
			gaps[i] += minDistance;
		}
		float[] points = new float[count];
		float current = points[0] = random.nextFloat();
		for (int i = 1; i < count; i++) {
			current += gaps[i - 1];
			points[i] = current % 1;
		}
		shuffle(points, random);
		return points;
	}

	private static void shuffle(final float[] list, final RandomSource random) {
		int size = list.length;

		for (int i = size; i > 1; i--) {
			int swapTo = random.nextInt(i);
			float temp = list[i - 1];
			list[i - 1] = list[swapTo];
			list[swapTo] = temp;
		}
	}

	public record AlleleRecord(String code, int index) {
		public static final Codec<AlleleRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("code").forGetter(AlleleRecord::code),
				Codec.INT.fieldOf("index").forGetter(AlleleRecord::index)
		).apply(instance, AlleleRecord::new));
	}
}
