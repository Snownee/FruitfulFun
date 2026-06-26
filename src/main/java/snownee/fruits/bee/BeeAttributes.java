package snownee.fruits.bee;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFBee;

public class BeeAttributes {
	public static final Codec<BeeAttributes> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.list(Codec.STRING).optionalFieldOf("Pollens", List.of()).forGetter(BeeAttributes::getPollens),
			GeneData.CODEC.fieldOf("Genes").forGetter(BeeAttributes::getGenes),
			Codec.list(UUIDUtil.CODEC).optionalFieldOf("Trusted", List.of()).forGetter(BeeAttributes::getTrusted),
			Identifier.CODEC.optionalFieldOf("Texture").forGetter($ -> Optional.ofNullable($.getTexture())),
			Codec.LONG.fieldOf("MutagenEndsIn").forGetter(BeeAttributes::getMutagenEndsIn)
	).apply(i, BeeAttributes::new));
	private static final Identifier SPEED_MODIFIER = FruitfulFun.id("bee_speed");
	private static final Identifier HEALTH_MODIFIER = FruitfulFun.id("bee_health");
	private static final Identifier DAMAGE_MODIFIER = FruitfulFun.id("bee_damage");
	private final List<String> pollens = Lists.newArrayList();
	private final GeneData genes;
	public boolean dirty = true;
	private List<UUID> trusted = List.of();
	@Nullable
	private Identifier texture;
	private long mutagenEndsIn;

	public static BeeAttributes of(Object bee) {
		return ((FFBee) bee).fruits$getBeeAttributes();
	}

	public BeeAttributes() {
		genes = new GeneData();
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BeeAttributes(List<String> pollens, GeneData genes, List<UUID> trusted, Optional<Identifier> texture, long mutagenEndsIn) {
		this.pollens.addAll(pollens);
		this.genes = genes;
		this.trusted = trusted;
		this.texture = texture.orElse(null);
		this.mutagenEndsIn = mutagenEndsIn;
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

	public boolean isSaddleable() {
		return hasTrait(Trait.MOUNTABLE);
	}

	public void dropSaddle(Bee bee) {
		if (bee.isSaddled() && bee.level() instanceof ServerLevel level) {
			bee.ejectPassengers();
			ItemStack saddle = bee.getItemBySlot(EquipmentSlot.SADDLE);
			bee.setItemSlot(EquipmentSlot.SADDLE, ItemStack.EMPTY);
			bee.spawnAtLocation(level, saddle);
		}
	}

	public boolean trusts(UUID uuid) {
		return trusted.contains(uuid);
	}

	public void updateTraits(Bee bee) {
		genes.updateTraits();
		updateTexture();
		if (bee.level().isClientSide()) {
			return;
		}

		AttributeInstance speedInstance = Objects.requireNonNull(bee.getAttribute(Attributes.FLYING_SPEED));
		AttributeInstance healthInstance = Objects.requireNonNull(bee.getAttribute(Attributes.MAX_HEALTH));
		AttributeInstance damageInstance = Objects.requireNonNull(bee.getAttribute(Attributes.ATTACK_DAMAGE));
		speedInstance.removeModifier(SPEED_MODIFIER);
		healthInstance.removeModifier(HEALTH_MODIFIER);
		damageInstance.removeModifier(DAMAGE_MODIFIER);
		if (hasTrait(Trait.FASTER)) {
			speedInstance.addPermanentModifier(
					new AttributeModifier(SPEED_MODIFIER, 0.25, AttributeModifier.Operation.ADD_VALUE));
		} else if (hasTrait(Trait.FAST)) {
			speedInstance.addPermanentModifier(
					new AttributeModifier(SPEED_MODIFIER, 0.15, AttributeModifier.Operation.ADD_VALUE));
		}
		boolean lazy = hasTrait(Trait.LAZY);
		if (lazy || hasTrait(Trait.WARRIOR)) {
			float healthRatio = bee.getHealth() / bee.getMaxHealth();
			if (lazy) {
				healthInstance.addPermanentModifier(
						new AttributeModifier(HEALTH_MODIFIER, 5, AttributeModifier.Operation.ADD_VALUE));
			} else {
				healthInstance.addPermanentModifier(
						new AttributeModifier(HEALTH_MODIFIER, 10, AttributeModifier.Operation.ADD_VALUE));
				damageInstance.addPermanentModifier(
						new AttributeModifier(DAMAGE_MODIFIER, 2, AttributeModifier.Operation.ADD_VALUE));
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

	public @Nullable Identifier getTexture() {
		return texture;
	}

	public void setTexture(@Nullable Identifier texture) {
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
