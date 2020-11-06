package snownee.fruits.plugin.hwyla;

import java.util.List;

import com.mojang.datafixers.util.Either;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.RenderableTextComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import snownee.fruits.FruitType;
import snownee.fruits.Hook;
import snownee.kiwi.util.NBTHelper;

public class BeePollenProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {

    public static final BeePollenProvider INSTANCE = new BeePollenProvider();

    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.get(HwylaPlugin.BEE) || !(accessor.getEntity() instanceof BeeEntity)) {
            return;
        }
        CompoundNBT data = accessor.getServerData();
        if (!data.contains("pollen")) {
            return;
        }
        ListNBT list = data.getList("pollen", Constants.NBT.TAG_STRING);
        List<Either<FruitType, Block>> pollen = Hook.readPollen(list);
        RenderableTextComponent[] components = new RenderableTextComponent[pollen.size()];
        int i = 0;
        for (Either<FruitType, Block> e : pollen) {
            components[i] = e.map(type -> HwylaPlugin.item(new ItemStack(type.fruit)), block -> HwylaPlugin.item(new ItemStack(block)));
            ++i;
        }
        tooltip.add(new RenderableTextComponent(components));
    }

    @Override
    public void appendServerData(CompoundNBT tag, ServerPlayerEntity player, World world, Entity entity) {
        if (entity instanceof BeeEntity) {
            NBTHelper data = NBTHelper.of(entity.getPersistentData());
            ListNBT list = data.getTagList("FruitsList", Constants.NBT.TAG_STRING);
            if (list != null) {
                tag.put("pollen", list);
            }
        }
    }

}
