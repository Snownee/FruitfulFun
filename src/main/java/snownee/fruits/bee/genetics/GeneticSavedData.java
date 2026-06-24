package snownee.fruits.bee.genetics;

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
		for (Allele allele : Allele.values()) {
			allele.codename = "0";
			allele.index = -1;
		}
		RandomSource random = RandomSource.create(seed);
		for (Allele allele : Allele.values()) {
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
			allele.color = Mth.hsvToRgb(allele.index / 254f, 0.86f, 0.86f);
		}
		Allele.BY_CODE = Allele.values().stream().sorted(Comparator.comparing(a -> a.codename)).toList();
	}

	public record AlleleRecord(String code, int index) {
		public static final Codec<AlleleRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("code").forGetter(AlleleRecord::code),
				Codec.INT.fieldOf("index").forGetter(AlleleRecord::index)
		).apply(instance, AlleleRecord::new));
	}
}
