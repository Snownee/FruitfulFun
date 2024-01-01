package snownee.fruits.compat.jade;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import snownee.fruits.FruitType;
import snownee.fruits.bee.BeeAttributes;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public class BeePollenProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {

	public static final BeePollenProvider INSTANCE = new BeePollenProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		CompoundTag data = accessor.getServerData();
		if (!data.contains("Pollens")) {
			return;
		}
		ListTag pollen = data.getList("Pollens", Tag.TAG_STRING);
		List<IElement> elements = Lists.newArrayList();
		for (Tag tag : pollen) {
			elements.add(IElementHelper.get().item(FruitType.getFruitOrDefault(tag.getAsString()).getDefaultInstance()));
		}
		tooltip.add(elements);
	}

	@Override
	public void appendServerData(CompoundTag data, EntityAccessor accessor) {
		BeeAttributes attributes = BeeAttributes.of(accessor.getEntity());
		List<String> pollens = attributes.getPollens();
		if (!pollens.isEmpty()) {
			ListTag list = new ListTag();
			for (String pollen : pollens) {
				list.add(StringTag.valueOf(pollen));
			}
			data.put("Pollens", list);
		}
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.BEE;
	}

}
