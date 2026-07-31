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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitType;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.BoundEntity;
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
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

public class InspectorProvider implements IServerDataProvider<EntityAccessor> {
	public static final Cache<Pair<UUID, UUID>, ListTag> POTENTIAL_CACHE = CacheBuilder.newBuilder().maximumSize(100).build();

	public static void appendServerData(Accessor<?> accessor, Bee bee) {
		ItemStack inspector = accessor.getPlayer().getUseItem();
		if (!BeeModule.INSPECTOR.is(inspector)) {
			return;
		}
		CompoundTag data = accessor.getServerData();
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.pollens();
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
		BoundEntity bound = inspector.get(BeeModule.BOUND_ENTITY.get());
		if (FFCommonConfig.inspectorShowOffspringPotential && bound != null && !bound.uuid().equals(bee.getUUID()) && BeeModule.canBreed(bee)) {
			Entity boundEntity = accessor.getLevel().getEntity(bound.uuid());
			if (boundEntity instanceof Bee boundBee && BeeModule.canBreed(boundBee)) {
				Pair<UUID, UUID> key = Pair.of(bound.uuid(), bee.getUUID());
				ListTag list = POTENTIAL_CACHE.getIfPresent(key);
				if (list == null) {
					POTENTIAL_CACHE.put(
							key,
							createPotentialList(
									list = new ListTag(),
									attributes.genes(),
									BeeAttributes.of(boundBee).genes(),
									accessor.getLevel().getRandom()));
				}
				data.put("Potential", list);
			}
		}
		ListTag list = new ListTag();
		for (Allele allele : Allele.sortedByCode()) {
			CompoundTag tag = new CompoundTag();
			Locus locus = attributes.getLocus(allele);
			tag.putString("Code", allele.codename);
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

	@Override
	public boolean shouldRequestData(EntityAccessor accessor) {
		return !InspectorClientHandler.isAnalyzing();
	}

	@Override
	public void appendServerData(CompoundTag data, EntityAccessor accessor) {
		appendServerData(accessor, (Bee) accessor.getEntity());
	}

	@Override
	public Identifier getUid() {
		return JadeCompat.INSPECTOR;
	}

	@Override
	public int getDefaultPriority() {
		return 5001;
	}

	public static abstract class CommonComponent<T extends Accessor<?>> extends InspectorProvider implements IComponentProvider<T> {
		public static void appendTooltip(ITooltip tooltip, Accessor<?> accessor) {
			CompoundTag data = accessor.getServerData();
			if (InspectorClientHandler.isAnalyzing()) {
				tooltip.add(Component.translatable("tip.fruitfulfun.analyzing"));
				Element icon = JadeUI.smallItem(Items.HONEYCOMB.getDefaultInstance()).narration("");
				int i = InspectorClientHandler.getHoverTicks() / 4 % 3;
				tooltip.append(JadeUI.spacer(2 + i * icon.getWidth(), icon.getHeight()));
				tooltip.append(icon);
				tooltip.append(JadeUI.spacer((2 - i) * icon.getWidth(), icon.getHeight()));
				return;
			}
			if (!data.contains("Loci")) {
				return;
			}
			tooltip.remove(JadeIds.MC_ENTITY_HEALTH);
			tooltip.remove(JadeIds.MC_ENTITY_ARMOR);
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
			tooltip.add(JadeUI.text(Component.translatable("tip.fruitfulfun.pressAlt")
					.withStyle(IThemeHelper.get().isLightColorScheme() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY)).scale(0.75F));
		}

		public static void showPollens(ITooltip tooltip, CompoundTag data) {
			ListTag pollens = data.getListOrEmpty("Pollens");
			title(tooltip, "text.fruitfulfun.pollen");
			if (pollens.isEmpty()) {
				tooltip.add(Component.translatable("text.fruitfulfun.pollen.none"));
			} else {
				List<Element> elements = Lists.newArrayList();
				for (Tag tag : pollens) {
					elements.add(JadeUI.item(FruitType.getFruitOrItem(tag.asString().orElseThrow()).getDefaultInstance()));
				}
				tooltip.add(elements);
			}
		}

		public static void showTraits(ITooltip tooltip, CompoundTag data) {
			ListTag traits = data.getListOrEmpty("Traits");
			title(tooltip, "text.fruitfulfun.trait");
			if (traits.isEmpty()) {
				tooltip.add(Component.translatable("text.fruitfulfun.trait.none"));
			} else {
				List<String> strings = Lists.newArrayList();
				for (Tag tag : traits) {
					Trait trait = Trait.REGISTRY.get(tag.asString().orElseThrow());
					if (trait != null) {
						strings.add(trait.displayName().getString());
					}
				}
				tooltip.add(Component.literal(String.join("/", strings)));
			}

			if (!data.contains("Potential")) {
				return;
			}
			title(tooltip, "text.fruitfulfun.offspringPotential");
			ListTag potential = data.getListOrEmpty("Potential");
			if (potential.isEmpty()) {
				tooltip.add(Component.translatable("text.fruitfulfun.offspringPotential.none"));
			} else {
				List<String> strings = Lists.newArrayList();
				for (CompoundTag tag : potential.compoundStream().toList()) {
					Trait trait = Trait.REGISTRY.get(tag.getStringOr("Trait", ""));
					if (trait == null) {
						continue;
					}
					String state = tag.getStringOr("State", "");
					strings.add(I18n.get("text.fruitfulfun.offspringPotential.%s".formatted(state), trait.displayName().getString()));
				}
				tooltip.add(Component.literal(String.join(" ", strings)));
			}
		}

		public static void showGenes(ITooltip tooltip, CompoundTag data, FFPlayer player) {
			title(tooltip, "text.fruitfulfun.gene");
			ListTag loci = data.getListOrEmpty("Loci");
			if (loci.isEmpty()) {
				return;
			}
			for (Tag e : loci) {
				CompoundTag tag = (CompoundTag) e;
				String code = tag.getString("Code").orElseThrow();
				String name = player.fruits$getGeneName(code);
				String desc = player.fruits$getGeneDesc(code);
				String high = name + (tag.getInt("High").orElseThrow() + 1);
				String low = name + (tag.getInt("Low").orElseThrow() + 1);
				if (desc.isEmpty()) {
					tooltip.add(Component.translatable("text.fruitfulfun.gene.pair", high, low));
				} else {
					tooltip.add(Component.translatable("text.fruitfulfun.gene.pairWithDesc", desc, high, low));
				}
			}
		}

		public static void title(ITooltip tooltip, String key) {
			tooltip.add(JadeUI.text(Component.translatable(key)).scale(0.75F));
			tooltip.add(JadeUI.spacer(2, 2));
		}

		@Override
		public boolean isRequired() {
			return true;
		}
	}

	public static class EntityComponent extends CommonComponent<EntityAccessor> implements IEntityComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
			appendTooltip(tooltip, accessor);
		}
	}

	public static class BlockComponent extends CommonComponent<BlockAccessor> implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			appendTooltip(tooltip, accessor);
		}
	}
}
