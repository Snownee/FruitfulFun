package snownee.fruits.bee;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;

public class BeeAttributes {
	private static final UUID SPEED_MODIFIER = UUID.fromString("d21ceda4-191f-47e7-a7c7-f5eff7012bdd");
	private static final UUID HEALTH_MODIFIER = UUID.fromString("aa3feeef-be3e-4d05-b98c-689bae6e22e7");
	private static final UUID DAMAGE_MODIFIER = UUID.fromString("168df6fe-fa8d-426f-8198-d89a5bc01397");
	private final List<String> pollens = Lists.newArrayList();
	private final Map<Allele, Locus> loci = Maps.newIdentityHashMap();
	private final Set<Trait> traits = Sets.newIdentityHashSet();
	public boolean dirty;
	private ItemStack saddle = ItemStack.EMPTY;
	private List<UUID> trusted = List.of();
	@Nullable
	private ResourceLocation texture;
	private long mutagenEndsIn;

	public static BeeAttributes of(Object bee) {
		return ((FFBee) bee).fruits$getBeeAttributes();
	}

	public void toNBT(CompoundTag data) {
		if (!saddle.isEmpty()) {
			data.put("Saddle", saddle.save(new CompoundTag()));
		}
		if (!trusted.isEmpty()) {
			ListTag trustedList = new ListTag();
			for (UUID uuid : trusted) {
				trustedList.add(StringTag.valueOf(uuid.toString()));
			}
			data.put("Trusted", trustedList);
		}
		if (!pollens.isEmpty()) {
			ListTag pollensList = new ListTag();
			for (String pollen : pollens) {
				pollensList.add(StringTag.valueOf(pollen));
			}
			data.put("Pollens", pollensList);
		}
		if (!loci.isEmpty()) {
			CompoundTag lociTag = new CompoundTag();
			for (Map.Entry<Allele, Locus> entry : loci.entrySet()) {
				lociTag.putByte(entry.getKey().name, entry.getValue().getData());
			}
			data.put("Genes", lociTag);
		}
	}

	public void fromNBT(CompoundTag data, Bee bee) {
		saddle = ItemStack.EMPTY;
		if (data.contains("Saddle")) {
			saddle = ItemStack.of(data.getCompound("Saddle"));
		}
		ImmutableList.Builder<UUID> builder = ImmutableList.builder();
		for (Tag tag : data.getList("Trusted", Tag.TAG_STRING)) {
			builder.add(UUID.fromString(tag.getAsString()));
		}
		trusted = builder.build();
		pollens.clear();
		for (Tag tag : data.getList("Pollens", Tag.TAG_STRING)) {
			pollens.add(tag.getAsString());
		}
		loci.clear();
		CompoundTag lociTag = data.getCompound("Genes");
		for (Allele allele : Allele.REGISTRY.values()) {
			Locus locus = new Locus(allele);
			if (lociTag.contains(allele.name)) {
				locus.setData(lociTag.getByte(allele.name));
			}
			loci.put(allele, locus);
		}
		updateTraits(bee);
	}

	public void setTrusted(List<UUID> trusted) {
		this.trusted = trusted;
		dirty = true;
	}

	public void addTrusted(UUID uuid) {
		if (trusted.contains(uuid)) {
			return;
		}
		setTrusted(ImmutableList.<UUID>builder().addAll(trusted).add(uuid).build());
	}

	public List<UUID> getTrusted() {
		return trusted;
	}

	public List<String> getPollens() {
		return pollens;
	}

	public boolean isSaddled() {
		return !saddle.isEmpty();
	}

	public boolean isSaddleable() {
		return hasTrait(Trait.MOUNTABLE);
	}

	public void setSaddle(ItemStack saddle) {
		this.saddle = saddle;
		dirty = true;
	}

	public ItemStack getSaddle() {
		return saddle;
	}

	public boolean trusts(UUID uuid) {
		return trusted.contains(uuid);
	}

	public void breedFrom(BeeAttributes parent1, Allele allele1, BeeAttributes parent2, Allele allele2, Bee bee) {
		RandomSource random = bee.getRandom();
		for (Allele allele : Allele.values()) {
			byte gene1 = parent1.pickAllele(allele, random, allele == allele1);
			byte gene2 = parent2.pickAllele(allele, random, allele == allele2);
			Locus locus = new Locus(allele);
			locus.setData((byte) (gene1 << 4 | gene2));
			loci.put(allele, locus);
		}
		updateTraits(bee);
	}

	public void updateTraits(Bee bee) {
		traits.clear();
		if (allGene(Allele.RAINC, 1)) {
			traits.add(Trait.RAIN_CAPABLE);
		}

		if (allGene(Allele.FANCY, 1)) {
			setTexture(new ResourceLocation(FruitfulFun.ID, "pink_bee"));
		} else if (allGene(Allele.FANCY, 2)) {
			setTexture(new ResourceLocation(FruitfulFun.ID, "wither_bee"));
			traits.add(Trait.WITHER_TOLERANT);
		} else {
			texture = null;
		}

		boolean lazy = false;
		if (allGene(Allele.FEAT1, 1)) {
			traits.add(Trait.LAZY);
			traits.add(Trait.MILD);
			lazy = true;
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

		if (bee.level().isClientSide) {
			return;
		}

		AttributeInstance speedInstance = Objects.requireNonNull(bee.getAttribute(Attributes.FLYING_SPEED));
		AttributeInstance healthInstance = Objects.requireNonNull(bee.getAttribute(Attributes.MAX_HEALTH));
		AttributeInstance damageInstance = Objects.requireNonNull(bee.getAttribute(Attributes.ATTACK_DAMAGE));
		speedInstance.removePermanentModifier(SPEED_MODIFIER);
		healthInstance.removePermanentModifier(HEALTH_MODIFIER);
		damageInstance.removePermanentModifier(DAMAGE_MODIFIER);
		if (hasTrait(Trait.FASTER)) {
			speedInstance.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER, "Genetic speed bonus", 0.25, AttributeModifier.Operation.ADDITION));
		} else if (hasTrait(Trait.FAST)) {
			speedInstance.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER, "Genetic speed bonus", 0.15, AttributeModifier.Operation.ADDITION));
		}
		if (lazy || hasTrait(Trait.WARRIOR)) {
			float healthRatio = bee.getHealth() / bee.getMaxHealth();
			if (lazy) {
				healthInstance.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER, "Genetic health bonus", 5, AttributeModifier.Operation.ADDITION));
			} else {
				healthInstance.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER, "Genetic health bonus", 10, AttributeModifier.Operation.ADDITION));
				damageInstance.addPermanentModifier(new AttributeModifier(DAMAGE_MODIFIER, "Genetic damage bonus", 2, AttributeModifier.Operation.ADDITION));
			}
			bee.setHealth(healthRatio * bee.getMaxHealth());
		}
		dirty = true;
	}

	public boolean hasTrait(Trait trait) {
		return traits.contains(trait);
	}

	public Locus getLocus(Allele allele) {
		return loci.computeIfAbsent(allele, Locus::new);
	}

	private byte pickAllele(Allele allele, RandomSource random, boolean highMutation) {
		Locus locus = getLocus(allele);
		int gene;
		if (random.nextBoolean()) {
			gene = locus.getHigh();
		} else {
			gene = locus.getLow();
		}
		return allele.maybeMutate((byte) gene, random, highMutation);
	}

	public @Nullable ResourceLocation getTexture() {
		return texture;
	}

	public void setTexture(@Nullable ResourceLocation texture) {
		this.texture = texture;
		dirty = true;
	}

	public boolean anyGene(Allele allele, int gene) {
		Locus locus = loci.get(allele);
		return locus.getHigh() == gene || locus.getLow() == gene;
	}

	public boolean allGene(Allele allele, int gene) {
		Locus locus = loci.get(allele);
		return locus.getHigh() == gene && locus.getLow() == gene;
	}

	public void randomize(Bee bee) {
		for (Allele type : Allele.values()) {
			Locus locus = new Locus(type);
			locus.randomize(bee.getRandom());
			loci.put(type, locus);
		}
		updateTraits(bee);
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

	public void setMutagenEndsIn(long mutagenEndsIn, long gameTime) {
		if (this.mutagenEndsIn == mutagenEndsIn) {
			return;
		}
		if (mutagenEndsIn == 0 && this.mutagenEndsIn <= gameTime) {
			return;
		}
		this.mutagenEndsIn = mutagenEndsIn;
		dirty = true;
	}

	public long getMutagenEndsIn() {
		return mutagenEndsIn;
	}
}
