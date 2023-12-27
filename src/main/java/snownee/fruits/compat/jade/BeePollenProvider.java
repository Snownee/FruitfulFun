package snownee.fruits.compat.jade;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitType;
import snownee.fruits.Hooks;
import snownee.fruits.hybridization.BeeAttributes;
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
		if (!config.get(JadeCompat.BEE) || !(accessor.getEntity() instanceof Bee)) {
			return;
		}
		CompoundTag data = accessor.getServerData();
		if (!data.contains("Pollens")) {
			return;
		}
		ListTag list = data.getList("Pollens", Tag.TAG_STRING);
		List<Block> pollen = Hooks.readPollen(list);
		List<IElement> elements = Lists.newArrayList();
		for (Block block : pollen) {
			elements.add(IElementHelper.get().item(FruitType.getFruitOrDefault(block).getDefaultInstance()));
		}
		tooltip.add(elements);
	}

	@Override
	public void appendServerData(CompoundTag data, EntityAccessor accessor) {
		if (accessor.getEntity() instanceof Bee bee) {
			BeeAttributes attributes = BeeAttributes.of(bee);
			List<String> pollens = attributes.getPollens();
			if (!pollens.isEmpty()) {
				ListTag list = new ListTag();
				for (String pollen : pollens) {
					list.add(StringTag.valueOf(pollen));
				}
				data.put("Pollens", list);
			}
		}
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.BEE;
	}

}
