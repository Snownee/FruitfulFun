package snownee.fruits.compat.jade;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Items;
import snownee.fruits.FruitType;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.Locus;
import snownee.fruits.bee.genetics.Trait;
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

	public static void appendServerData(Accessor<?> accessor, Bee bee) {
		if (!BeeModule.INSPECTOR.is(accessor.getPlayer().getUseItem())) {
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
		Set<Trait> traits = attributes.getTraits();
		if (!traits.isEmpty()) {
			ListTag list = new ListTag();
			for (Trait trait : traits) {
				list.add(StringTag.valueOf(trait.name()));
			}
			data.put("Traits", list);
		}
		Map<Allele, Locus> loci = attributes.getLoci();
		ListTag list = new ListTag();
		for (Allele allele : Allele.sortedByCode()) {
			Locus locus = loci.get(allele);
			String first = String.valueOf(allele.codename) + (locus.getHigh() + 1);
			String second = String.valueOf(allele.codename) + (locus.getLow() + 1);
			list.add(StringTag.valueOf(" -" + first + " / " + second));
		}
		data.put("Loci", list);
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
				showGenes(tooltip, data);
				break;
		}
		tooltip.add(new ScaledTextElement(Component.translatable("tip.fruitfulfun.press_alt")
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
				elements.add(IElementHelper.get().item(FruitType.getFruitOrDefault(tag.getAsString()).getDefaultInstance()));
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
	}

	public static void showGenes(ITooltip tooltip, CompoundTag data) {
		ListTag loci = data.getList("Loci", Tag.TAG_STRING);
		title(tooltip, "text.fruitfulfun.gene");
		if (loci.isEmpty()) {
			return;
		}
		for (Tag tag : loci) {
			tooltip.add(Component.literal(tag.getAsString()));
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
}
