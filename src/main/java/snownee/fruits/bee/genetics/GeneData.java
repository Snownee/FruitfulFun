package snownee.fruits.bee.genetics;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import snownee.fruits.Hooks;

public class GeneData {
	protected final Map<Allele, Locus> loci = Maps.newIdentityHashMap();
	protected final Set<Trait> traits = Sets.newIdentityHashSet();

	public void updateTraits() {
		traits.clear();
		if (allGene(Allele.RAINC, 1)) {
			traits.add(Trait.RAIN_CAPABLE);
		}

		if (allGene(Allele.FANCY, 1)) {
			traits.add(Trait.PINK);
		} else if (allGene(Allele.FANCY, 2)) {
			traits.add(Trait.WITHER_TOLERANT);
		}

		if (allGene(Allele.FEAT1, 1)) {
			traits.add(Trait.LAZY);
			traits.add(Trait.MILD);
		} else if (anyGene(Allele.FEAT1, 1)) {
			traits.add(Trait.MILD);
		}

		if (allGene(Allele.FEAT1, 2)) {
			traits.add(Trait.FASTER);
		} else if (anyGene(Allele.FEAT1, 2)) {
			traits.add(Trait.FAST);
		}

		if (anyGene(Allele.FEAT1, 2) && !hasTrait(Trait.MILD)) {
			traits.add(Trait.WARRIOR);
		} else if (allGene(Allele.FEAT2, 1)) {
			traits.add(Trait.ADVANCED_POLLINATION);
		}

		if (allGene(Allele.FEAT2, 2)) {
			traits.add(Trait.MOUNTABLE);
		}
	}

	public boolean hasTrait(Trait trait) {
		return Hooks.bee && traits.contains(trait);
	}

	public Locus getLocus(Allele allele) {
		return loci.computeIfAbsent(allele, Locus::new);
	}

	public boolean anyGene(Allele allele, int gene) {
		Locus locus = getLocus(allele);
		return locus.getHigh() == gene || locus.getLow() == gene;
	}

	public boolean allGene(Allele allele, int gene) {
		Locus locus = getLocus(allele);
		return locus.getHigh() == gene && locus.getLow() == gene;
	}

	public void randomize(RandomSource random) {
		for (Allele type : Allele.values()) {
			Locus locus = new Locus(type);
			locus.randomize(random);
			loci.put(type, locus);
		}
	}

	public Map<Allele, Locus> getLoci() {
		return loci;
	}

	public Set<Trait> getTraits() {
		return traits;
	}

	public void setTraits(List<Trait> list) {
		traits.clear();
		traits.addAll(list);
	}

	public void breedFrom(GeneData parent1, Allele allele1, GeneData parent2, Allele allele2, RandomSource random) {
		for (Allele allele : Allele.values()) {
			byte gene1 = parent1.pickAllele(allele, random, allele == allele1);
			byte gene2 = parent2.pickAllele(allele, random, allele == allele2);
			Locus locus = new Locus(allele);
			locus.setData((byte) (gene1 << 4 | gene2));
			loci.put(allele, locus);
		}
	}

	protected byte pickAllele(Allele allele, RandomSource random, boolean highMutation) {
		Locus locus = getLocus(allele);
		int gene;
		if (random.nextBoolean()) {
			gene = locus.getHigh();
		} else {
			gene = locus.getLow();
		}
		return allele.maybeMutate((byte) gene, random, highMutation);
	}

	public void toNBT(CompoundTag lociTag) {
		for (Map.Entry<Allele, Locus> entry : loci.entrySet()) {
			lociTag.putByte(entry.getKey().name, entry.getValue().getData());
		}
	}

	public void fromNBT(CompoundTag lociTag) {
		loci.clear();
		for (Allele allele : Allele.REGISTRY.values()) {
			Locus locus = new Locus(allele);
			if (lociTag.contains(allele.name)) {
				locus.setData(lociTag.getByte(allele.name));
			}
			loci.put(allele, locus);
		}
	}
}
