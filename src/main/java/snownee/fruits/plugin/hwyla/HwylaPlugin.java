package snownee.fruits.plugin.hwyla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import snownee.fruits.Fruits;

@WailaPlugin
public class HwylaPlugin implements IWailaPlugin {

    static final ResourceLocation ITEM = new ResourceLocation("item");
    public static final ResourceLocation BEE = new ResourceLocation(Fruits.MODID, "bee");

    public static RenderableTextComponent item(ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();
        ResourceLocation id = stack.getItem().getRegistryName();
        tag.putString("id", id == null ? "minecraft:air" : id.toString());
        tag.putInt("count", stack.getCount());
        if (stack.hasTag())
            tag.putString("nbt", stack.getTag().toString());
        return new RenderableTextComponent(ITEM, tag);
    }

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(BeePollenProvider.INSTANCE, TooltipPosition.BODY, BeeEntity.class);
        registrar.registerEntityDataProvider(BeePollenProvider.INSTANCE, BeeEntity.class);
        registrar.addConfig(BEE, true);
    }
}
