package snownee.fruits.bee.genetics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import net.minecraft.util.RandomSource;
import snownee.fruits.Hooks;

public class GeneData {
	public static final Codec<GeneData> CODEC = Codec.of(GeneData::encode, GeneData::decode);

	protected final Map<Allele, Locus> loci = Maps.newIdentityHashMap();
	protected final Set<Trait> traits = Sets.newIdentityHashSet();
	protected Set<Trait> extraTraits = Set.of();

	public void updateTraits() {
		traits.clear();
		traits.addAll(extraTraits);
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

		if (allGene(Allele.FEAT2, 2) && !hasTrait(Trait.GHOST)) {
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

	public void breedFrom(GeneData parent1, @Nullable Allele allele1, GeneData parent2, @Nullable Allele allele2, RandomSource random) {
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

	public void addExtraTrait(Trait trait) {
		if (extraTraits.isEmpty()) {
			extraTraits = Sets.newIdentityHashSet();
		}
		extraTraits.add(trait);
		traits.add(trait);
	}

	public void removeExtraTrait(Trait trait) {
		if (!extraTraits.isEmpty()) {
			extraTraits.remove(trait);
		}
	}

	private static <T> DataResult<Pair<GeneData, T>> decode(DynamicOps<T> ops, T t) {
		MapLike<T> map = ops.getMap(t).getOrThrow();
		GeneData geneData = new GeneData();
		for (Allele allele : Allele.REGISTRY.values()) {
			T input = map.get(allele.name);
			if (input != null) {
				geneData.getLocus(allele).setData(ops.getNumberValue(input).getOrThrow().byteValue());
			}
		}
		T input = map.get("ExtraTraits");
		if (input != null) {
			ops.getStream(input)
					.getOrThrow()
					.map(ops::getStringValue)
					.map(DataResult::getOrThrow)
					.map(Trait.REGISTRY::get)
					.filter(Objects::nonNull)
					.forEach(geneData::addExtraTrait);
		}
		geneData.updateTraits();
		return DataResult.success(Pair.of(geneData, t));
	}

	private <T> DataResult<T> encode(DynamicOps<T> ops, T t) {
		RecordBuilder<T> mapBuilder = ops.mapBuilder();
		for (Map.Entry<Allele, Locus> entry : loci.entrySet()) {
			mapBuilder.add(entry.getKey().name, ops.createByte(entry.getValue().getData()));
		}
		if (!extraTraits.isEmpty()) {
			mapBuilder.add("ExtraTraits", ops.createList(extraTraits.stream().map(Trait::name).map(ops::createString)));
		}
		return mapBuilder.build(t);
	}
}
