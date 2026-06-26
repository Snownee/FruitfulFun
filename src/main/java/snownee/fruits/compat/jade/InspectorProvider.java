package snownee.fruits.compat.jade;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.Items;
import snownee.fruits.FruitType;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.Locus;
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
		Set<Trait> traits = attributes.getGenes().getTraits();
		if (!traits.isEmpty()) {
			ListTag list = new ListTag();
			for (Trait trait : traits) {
				list.add(StringTag.valueOf(trait.name()));
			}
			data.put("Traits", list);
		}
		ListTag list = new ListTag();
		for (Allele allele : Allele.sortedByCode()) {
			CompoundTag tag = new CompoundTag();
			Locus locus = attributes.getLocus(allele);
			tag.putString("Code", allele.codename);
			tag.putInt("High", locus.getHigh());
			tag.putInt("Low", locus.getLow());
			list.add(tag);
		}
		data.put("Loci", list);
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
						strings.add(trait.getDisplayName().getString());
					}
				}
				tooltip.add(Component.literal(String.join("/", strings)));
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
