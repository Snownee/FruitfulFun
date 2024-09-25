package snownee.fruits.bee;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFBee;

public class BeeAttributes {
	private static final UUID SPEED_MODIFIER = UUID.fromString("d21ceda4-191f-47e7-a7c7-f5eff7012bdd");
	private static final UUID HEALTH_MODIFIER = UUID.fromString("aa3feeef-be3e-4d05-b98c-689bae6e22e7");
	private static final UUID DAMAGE_MODIFIER = UUID.fromString("168df6fe-fa8d-426f-8198-d89a5bc01397");
	private final List<String> pollens = Lists.newArrayList();
	private final GeneData genes = new GeneData();
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
		CompoundTag lociTag = new CompoundTag();
		genes.toNBT(lociTag);
		if (!lociTag.isEmpty()) {
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
		if (data.contains("Genes")) {
			genes.fromNBT(data.getCompound("Genes"));
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

	public void dropSaddle(Bee bee) {
		if (isSaddled()) {
			bee.ejectPassengers();
			bee.spawnAtLocation(saddle);
			setSaddle(ItemStack.EMPTY);
		}
	}

	public boolean trusts(UUID uuid) {
		return trusted.contains(uuid);
	}

	public void updateTraits(Bee bee) {
		genes.updateTraits();
		updateTexture();
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
			speedInstance.addPermanentModifier(
					new AttributeModifier(SPEED_MODIFIER, "Genetic speed bonus", 0.25, AttributeModifier.Operation.ADDITION));
		} else if (hasTrait(Trait.FAST)) {
			speedInstance.addPermanentModifier(
					new AttributeModifier(SPEED_MODIFIER, "Genetic speed bonus", 0.15, AttributeModifier.Operation.ADDITION));
		}
		boolean lazy = hasTrait(Trait.LAZY);
		if (lazy || hasTrait(Trait.WARRIOR)) {
			float healthRatio = bee.getHealth() / bee.getMaxHealth();
			if (lazy) {
				healthInstance.addPermanentModifier(
						new AttributeModifier(HEALTH_MODIFIER, "Genetic health bonus", 5, AttributeModifier.Operation.ADDITION));
			} else {
				healthInstance.addPermanentModifier(
						new AttributeModifier(HEALTH_MODIFIER, "Genetic health bonus", 10, AttributeModifier.Operation.ADDITION));
				damageInstance.addPermanentModifier(
						new AttributeModifier(DAMAGE_MODIFIER, "Genetic damage bonus", 2, AttributeModifier.Operation.ADDITION));
			}
			bee.setHealth(healthRatio * bee.getMaxHealth());
		}
		dirty = true;
	}

	public void updateTexture() {
		if (hasTrait(Trait.GHOST)) {
			setTexture(FruitfulFun.id("ghost_bee"));
		} else if (hasTrait(Trait.PINK)) {
			setTexture(FruitfulFun.id("pink_bee"));
		} else if (hasTrait(Trait.WITHER_TOLERANT)) {
			setTexture(FruitfulFun.id("wither_bee"));
		} else {
			setTexture(null);
		}
	}

	public @Nullable ResourceLocation getTexture() {
		return texture;
	}

	public void setTexture(@Nullable ResourceLocation texture) {
		this.texture = texture;
		dirty = true;
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

	public boolean hasTrait(Trait trait) {
		return genes.hasTrait(trait);
	}

	public GeneData getGenes() {
		return genes;
	}

	public Locus getLocus(Allele allele) {
		return genes.getLocus(allele);
	}

	public void randomize(Bee bee) {
		genes.randomize(bee.getRandom());
		updateTraits(bee);
	}
}
