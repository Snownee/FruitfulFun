package snownee.fruits.bee.genetics;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;

public class GeneticSavedData extends SavedData {
	private final Map<String, AlleleRecord> alleles = Maps.newHashMap();

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		CompoundTag alleleTag = new CompoundTag();
		for (Map.Entry<String, AlleleRecord> entry : alleles.entrySet()) {
			CompoundTag recordTag = new CompoundTag();
			recordTag.putString("Code", entry.getValue().code);
			recordTag.putInt("Index", entry.getValue().index);
			alleleTag.put(entry.getKey(), recordTag);
		}
		compoundTag.put("Alleles", alleleTag);
		return compoundTag;
	}

	public static GeneticSavedData load(CompoundTag compoundTag) {
		GeneticSavedData data = new GeneticSavedData();
		CompoundTag alleleTag = compoundTag.getCompound("Alleles");
		Set<String> knownCodes = Sets.newHashSetWithExpectedSize(alleleTag.size());
		for (String key : alleleTag.getAllKeys()) {
			CompoundTag recordTag = alleleTag.getCompound(key);
			String code = recordTag.getString("Code");
			if (knownCodes.add(code)) {
				data.alleles.put(key, new AlleleRecord(code, recordTag.getInt("Index")));
			}
		}
		data.setDirty();
		return data;
	}

	public void initAlleles(long seed) {
		for (Allele allele : Allele.values()) {
			allele.codename = '0';
			allele.index = -1;
		}
		RandomSource random = RandomSource.create(seed);
		for (Allele allele : Allele.values()) {
			AlleleRecord alleleRecord = alleles.get(allele.name);
			if (alleleRecord != null) {
				allele.codename = alleleRecord.code.charAt(0);
				allele.index = alleleRecord.index;
				random.nextInt(26);
				random.nextInt(255);
			} else {
				int codename = random.nextInt(26);
				while (Allele.byCode((char) ('A' + codename)) != null) {
					codename = (codename + 1) % 26;
				}
				allele.codename = (char) ('A' + codename);
				int index = random.nextInt(255);
				while (Allele.byIndex(index) != null) {
					index = (index + 31) % 255;
				}
				allele.index = index;
				alleles.put(allele.name, new AlleleRecord(String.valueOf(allele.codename), allele.index));
				setDirty();
			}
			allele.color = Mth.hsvToRgb(allele.index / 254f, 0.86f, 0.86f);
		}
		Allele.BY_CODE = Allele.values().stream().sorted(Comparator.comparingInt(a -> a.codename)).toList();
	}

	public record AlleleRecord(String code, int index) {
	}
}
