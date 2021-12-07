package snownee.fruits.plugin.jade;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;

import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitType;
import snownee.fruits.Hooks;
import snownee.kiwi.util.NBTHelper;

public class BeePollenProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {

	public static final BeePollenProvider INSTANCE = new BeePollenProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!config.get(JadePlugin.BEE) || !(accessor.getEntity() instanceof Bee)) {
			return;
		}
		CompoundTag data = accessor.getServerData();
		if (!data.contains("pollen")) {
			return;
		}
		ListTag list = data.getList("pollen", Tag.TAG_STRING);
		List<Either<FruitType, Block>> pollen = Hooks.readPollen(list);
		List<IElement> elements = Lists.newArrayList();
		IElementHelper helper = tooltip.getElementHelper();
		for (Either<FruitType, Block> e : pollen) {
			ItemStack stack = new ItemStack(e.map(type -> type.fruit, block -> block));
			elements.add(helper.item(stack));
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

}
