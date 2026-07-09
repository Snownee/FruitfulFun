package snownee.fruits.bee.genetics;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.bee.Bee;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.bee.BeeModule;

@FunctionalInterface
public interface MutationRate {
	MutationRate DEFAULT = (allele, random) -> allele.mutationRate > 0 && random.nextFloat() < allele.mutationRate;

	boolean shouldMutate(Allele allele, RandomSource random);

	static MutationRate mutagenAffected(Bee bee) {
		MobEffectInstance effect = bee.getEffect(BeeModule.MUTAGEN_EFFECT.holderOrThrow());
		if (effect == null) {
			return DEFAULT;
		}
		Allele affectedAllele = Allele.byIndex(effect.getAmplifier());
		return (allele, random) -> {
			if (allele == affectedAllele) {
				return FFCommonConfig.mutagenMutationRate > 0 && random.nextFloat() < FFCommonConfig.mutagenMutationRate;
			}
			return DEFAULT.shouldMutate(allele, random);
		};
	}

	static MutationRate neverMutate() {
		return (_, _) -> false;
	}
}
