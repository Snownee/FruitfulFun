package snownee.fruits.compat.jade;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitType;
import snownee.fruits.Hooks;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.kiwi.util.NBTHelper;

public class BeePollenProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {

	public static final BeePollenProvider INSTANCE = new BeePollenProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!config.get(JadeCompat.BEE) || !(accessor.getEntity() instanceof Bee)) {
			return;
		}
		CompoundTag data = accessor.getServerData();
		if (!data.contains("pollen")) {
			return;
		}
		ListTag list = data.getList("pollen", Tag.TAG_STRING);
		List<Block> pollen = Hooks.readPollen(list);
		List<IElement> elements = Lists.newArrayList();
		for (Block block : pollen) {
			elements.add(IElementHelper.get().item(FruitType.getFruitOrDefault(block).getDefaultInstance()));
		}
		tooltip.add(elements);
	}

	@Override
	public void appendServerData(CompoundTag tag, ServerPlayer player, Level world, Entity entity, boolean showDetails) {
		if (entity instanceof Bee) {
			NBTHelper data = NBTHelper.of(entity.getPersistentData());
			ListTag list = data.getTagList("FruitsList", Tag.TAG_STRING);
			if (list != null) {
				tag.put("pollen", list);
			}
		}
	}

	@Override
	public ResourceLocation getUid() {
		return JadeCompat.BEE;
	}

}
