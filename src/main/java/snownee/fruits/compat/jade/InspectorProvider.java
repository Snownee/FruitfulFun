package snownee.fruits.compat.jade;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitType;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.MutationRate;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFPlayer;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ScaledTextElement;

public class InspectorProvider implements IEntityComponentProvider, IBlockComponentProvider, IServerDataProvider<EntityAccessor> {

	public static final Cache<Pair<UUID, UUID>, ListTag> POTENTIAL_CACHE = CacheBuilder.newBuilder().maximumSize(100).build();

	public static void appendServerData(Accessor<?> accessor, Bee bee) {
		ItemStack inspector = accessor.getPlayer().getUseItem();
		if (!BeeModule.INSPECTOR.is(inspector)) {
			return;
		}
		CompoundTag data = accessor.getServerData();
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		if (!pollens.isEmpty()) {
			ListTag list = new ListTag();
			for (String pollen : pollens) {
				list.add(StringTag.valueOf(pollen));
			}
			data.put("Pollens", list);
		}
		Set<Trait> traits = attributes.genes().traits();
		if (!traits.isEmpty()) {
			ListTag list = new ListTag();
			for (Trait trait : traits) {
				list.add(StringTag.valueOf(trait.name()));
			}
			data.put("Traits", list);
		}
		CompoundTag inspectorTag = inspector.getTag();
		if (FFCommonConfig.inspectorShowOffspringPotential && inspectorTag != null && inspectorTag.hasUUID("BoundEntityUUID")
				&& BeeModule.canBreed(bee)) {
			UUID boundUuid = inspectorTag.getUUID("BoundEntityUUID");
			if (!boundUuid.equals(bee.getUUID())) {
				Level level = accessor.getLevel();
				Entity boundEntity = ((ServerLevel) level).getEntity(boundUuid);
				if (boundEntity instanceof Bee boundBee && BeeModule.canBreed(boundBee)) {
					Pair<UUID, UUID> key = Pair.of(boundUuid, bee.getUUID());
					ListTag list = POTENTIAL_CACHE.getIfPresent(key);
					if (list == null) {
						POTENTIAL_CACHE.put(
								key,
								createPotentialList(
										list = new ListTag(),
										attributes.genes(),
										BeeAttributes.of(boundBee).genes(),
										level.getRandom()));
					}
					data.put("Potential", list);
				}
			}
		}
		ListTag list = new ListTag();
		for (Allele allele : Allele.sortedByCode()) {
			CompoundTag tag = new CompoundTag();
			Locus locus = attributes.getLocus(allele);
			tag.putString("Code", String.valueOf(allele.codename));
			tag.putInt("High", locus.high());
			tag.putInt("Low", locus.low());
			list.add(tag);
		}
		data.put("Loci", list);
	}

	private static ListTag createPotentialList(ListTag tag, GeneData genes1, GeneData genes2, RandomSource random) {
		Set<String> potentialTraits = Sets.newHashSet();
		genes1.traits().forEach(trait -> potentialTraits.add(trait.name()));
		genes2.traits().forEach(trait -> potentialTraits.add(trait.name()));
		Object2IntOpenHashMap<String> traitCounts = new Object2IntOpenHashMap<>();
		for (String trait : potentialTraits) {
			traitCounts.put(trait, 0);
		}
		for (int i = 0; i < 200; i++) {
			GeneData offspring = new GeneData();
			offspring.breedFrom(
					genes1,
					MutationRate.neverMutate(),
					genes2,
					MutationRate.neverMutate(),
					random);
			offspring.updateTraits();
			for (Trait trait : offspring.traits()) {
				potentialTraits.add(trait.name());
				traitCounts.addTo(trait.name(), 1);
			}
		}
		List<Pair<String, String>> results = Lists.newArrayList();
		for (String trait : potentialTraits) {
			int count = traitCounts.getInt(trait);
			int parentTraitBasis = 0;
			if (genes1.hasTrait(Trait.REGISTRY.get(trait))) {
				parentTraitBasis += 100;
			}
			if (genes2.hasTrait(Trait.REGISTRY.get(trait))) {
				parentTraitBasis += 100;
			}
			int gap = count - parentTraitBasis;
			if (gap > 0) {
				results.add(Pair.of(trait, gap >= parentTraitBasis / 2 ? "++" : "+"));
			} else if (gap < 0) {
				results.add(Pair.of(trait, gap <= -parentTraitBasis / 2 ? "--" : "-"));
			}
		}
		results.sort(Comparator.<Pair<String, String>>comparingInt(pair -> pair.getSecond().length())
				.thenComparing(Pair::getFirst));
		int limit = 3;
		for (Pair<String, String> pair : results) {
			CompoundTag traitTag = new CompoundTag();
			traitTag.putString("Trait", pair.getFirst());
			traitTag.putString("State", pair.getSecond());
			tag.add(traitTag);
			if (--limit <= 0) {
				break;
			}
		}
		return tag;
	}

	public static void appendTooltip(ITooltip tooltip, Accessor<?> accessor) {
		CompoundTag data = accessor.getServerData();
		if (InspectorClientHandler.isAnalyzing()) {
			tooltip.add(Component.translatable("tip.fruitfulfun.analyzing"));
			IElementHelper elements = IElementHelper.get();
			IElement icon = elements.smallItem(Items.HONEYCOMB.getDefaultInstance()).message(null);
			int i = InspectorClientHandler.getHoverTicks() / 4 % 3;
			tooltip.append(elements.spacer(2 + i * (int) icon.getCachedSize().x, (int) icon.getCachedSize().y));
			tooltip.append(icon);
			tooltip.append(elements.spacer((2 - i) * (int) icon.getCachedSize().x, (int) icon.getCachedSize().y));
			return;
		}
		if (!data.contains("Loci")) {
			return;
		}
		tooltip.remove(Identifiers.MC_ENTITY_HEALTH);
		tooltip.remove(Identifiers.MC_ENTITY_ARMOR);
		switch (InspectorClientHandler.getPageNow()) {
			case 0:
				showPollens(tooltip, data);
				break;
			case 1:
				showTraits(tooltip, data);
				break;
			case 2:
				showGenes(tooltip, data, FFPlayer.of(accessor.getPlayer()));
				break;
		}
		tooltip.add(new ScaledTextElement(
				Component.translatable("tip.fruitfulfun.pressAlt")
						.withStyle(IThemeHelper.get().isLightColorScheme() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY), 0.75f));
	}

	public static void showPollens(ITooltip tooltip, CompoundTag data) {
		ListTag pollens = data.getList("Pollens", Tag.TAG_STRING);
		title(tooltip, "text.fruitfulfun.pollen");
		if (pollens.isEmpty()) {
			tooltip.add(Component.translatable("text.fruitfulfun.pollen.none"));
		} else {
			List<IElement> elements = Lists.newArrayList();
			for (Tag tag : pollens) {
				elements.add(IElementHelper.get().item(FruitType.getFruitOrItem(tag.getAsString()).getDefaultInstance()));
			}
			tooltip.add(elements);
		}
	}

	public static void showTraits(ITooltip tooltip, CompoundTag data) {
		ListTag traits = data.getList("Traits", Tag.TAG_STRING);
		title(tooltip, "text.fruitfulfun.trait");
		if (traits.isEmpty()) {
			tooltip.add(Component.translatable("text.fruitfulfun.trait.none"));
		} else {
			List<String> strings = Lists.newArrayList();
			for (Tag tag : traits) {
				Trait trait = Trait.REGISTRY.get(tag.getAsString());
				if (trait != null) {
					strings.add(trait.getDisplayName().getString());
				}
			}
			tooltip.add(Component.literal(String.join("/", strings)));
		}

		if (!data.contains("Potential")) {
			return;
		}
		title(tooltip, "text.fruitfulfun.offspringPotential");
		ListTag potential = data.getList("Potential", Tag.TAG_COMPOUND);
		if (potential.isEmpty()) {
			tooltip.add(Component.translatable("text.fruitfulfun.offspringPotential.none"));
		} else {
			List<String> strings = Lists.newArrayList();
			for (Tag e : potential) {
				CompoundTag tag = (CompoundTag) e;
				Trait trait = Trait.REGISTRY.get(tag.getString("Trait"));
				if (trait == null) {
					continue;
				}
				String state = tag.getString("State");
				strings.add(Component.translatable(
						"text.fruitfulfun.offspringPotential." + state,
						trait.getDisplayName().getString()).getString());
			}
			tooltip.add(Component.literal(String.join(" ", strings)));
		}
	}

	public static void showGenes(ITooltip tooltip, CompoundTag data, FFPlayer player) {
		title(tooltip, "text.fruitfulfun.gene");
		ListTag loci = data.getList("Loci", Tag.TAG_COMPOUND);
		if (loci.isEmpty()) {
			return;
		}
		for (Tag e : loci) {
			CompoundTag tag = (CompoundTag) e;
			String code = tag.getString("Code");
			String name = player.fruits$getGeneName(code);
			String desc = player.fruits$getGeneDesc(code);
			String high = name + (tag.getInt("High") + 1);
			String low = name + (tag.getInt("Low") + 1);
			if (desc.isEmpty()) {
				tooltip.add(Component.translatable("text.fruitfulfun.gene.pair", high, low));
			} else {
				tooltip.add(Component.translatable("text.fruitfulfun.gene.pairWithDesc", desc, high, low));
			}
		}
	}

	public static void title(ITooltip tooltip, String key) {
		tooltip.add(new ScaledTextElement(Component.translatable(key), 0.75f));
		tooltip.add(IElementHelper.get().spacer(2, 2));
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		appendTooltip(tooltip, accessor);
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		appendTooltip(tooltip, accessor);
	}

	@Override
	public void appendServerData(CompoundTag data, EntityAccessor accessor) {
		appendServerData(accessor, (Bee) accessor.getEntity());
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.INSPECTOR;
	}

	@Override
	public int getDefaultPriority() {
		return 5001;
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
