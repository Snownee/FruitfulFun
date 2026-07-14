package snownee.fruits.bee;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFBee;

public class BeeAttributes {
	public static final Codec<BeeAttributes> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.list(Codec.STRING).optionalFieldOf("Pollens", List.of()).forGetter(BeeAttributes::pollens),
			GeneData.CODEC.fieldOf("Genes").forGetter(BeeAttributes::genes),
			Codec.list(UUIDUtil.CODEC).optionalFieldOf("Trusted", List.of()).forGetter(BeeAttributes::getTrusted),
			Codec.list(UUIDUtil.CODEC).optionalFieldOf("Inspected", List.of()).forGetter(BeeAttributes::getInspected),
			BeeVariant.CODEC.optionalFieldOf("ForcedVariant").forGetter($ -> Optional.ofNullable($.forcedVariant)),
			Codec.LONG.optionalFieldOf("MutagenEndsIn", 0L).forGetter(BeeAttributes::getMutagenEndsIn)
	).apply(i, BeeAttributes::new));

	private static final Identifier SPEED_MODIFIER = FruitfulFun.id("bee_speed");
	private static final Identifier HEALTH_MODIFIER = FruitfulFun.id("bee_health");
	private static final Identifier DAMAGE_MODIFIER = FruitfulFun.id("bee_damage");
	private final List<String> pollens = Lists.newArrayList();
	private final GeneData genes;
	public boolean dirty = true;
	private List<UUID> trusted = List.of();
	private Set<UUID> inspected = Set.of();
	@Nullable
	private Holder<BeeVariant> variant;
	@Nullable
	private Holder<BeeVariant> forcedVariant;
	private long mutagenEndsIn;

	public static BeeAttributes of(Object bee) {
		return ((FFBee) bee).fruits$getBeeAttributes();
	}

	public BeeAttributes() {
		genes = new GeneData();
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BeeAttributes(
			List<String> pollens,
			GeneData genes,
			List<UUID> trusted,
			List<UUID> inspected,
			Optional<Holder<BeeVariant>> forcedVariant,
			long mutagenEndsIn) {
		this.pollens.addAll(pollens);
		this.genes = genes;
		this.trusted = trusted;
		this.inspected = Set.copyOf(inspected);
		this.forcedVariant = forcedVariant.orElse(null);
		this.mutagenEndsIn = mutagenEndsIn;
		dirty = true;
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

	public void addInspected(UUID uuid) {
		if (inspected.contains(uuid)) {
			return;
		}
		int max = 20;
		ImmutableSet.Builder<UUID> builder = ImmutableSet.builderWithExpectedSize(Math.min(inspected.size() + 1, max));
		builder.add(uuid);
		for (UUID id : inspected) {
			if (builder.build().size() >= max) {
				break;
			}
			builder.add(id);
		}
		inspected = builder.build();
	}

	public List<UUID> getInspected() {
		return List.copyOf(inspected);
	}

	public boolean isInspected(Player player) {
		return inspected.contains(player.getUUID());
	}

	public List<String> pollens() {
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
		updateTexture(bee.registryAccess().lookupOrThrow(FFRegistries.BEE_VARIANT_KEY));
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

	public void updateTexture(HolderGetter<BeeVariant> holderGetter) {
		setVariant(holderGetter, BeeVariants.NORMAL);
		maybeUseVariant(holderGetter, Trait.GHOST);
		maybeUseVariant(holderGetter, Trait.PINK);
		maybeUseVariant(holderGetter, Trait.WITHER_TOLERANT);
	}

	private void maybeUseVariant(HolderGetter<BeeVariant> holderGetter, Trait trait) {
		Preconditions.checkArgument(trait.variant().isPresent(), "Trait %s does not have a variant", trait);
		if (variant == null || variant.is(BeeVariants.NORMAL) && hasTrait(trait)) {
			setVariant(holderGetter, trait.variant().get());
		}
	}

	public Holder<BeeVariant> variant() {
		return forcedVariant != null ? forcedVariant : Objects.requireNonNull(variant);
	}

	public void setForcedVariant(Holder<BeeVariant> forcedVariant) {
		this.forcedVariant = forcedVariant;
		dirty = true;
	}

	private void setVariant(HolderGetter<BeeVariant> holderGetter, ResourceKey<BeeVariant> variant) {
		this.variant = holderGetter.getOrThrow(variant);
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

	public GeneData genes() {
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
