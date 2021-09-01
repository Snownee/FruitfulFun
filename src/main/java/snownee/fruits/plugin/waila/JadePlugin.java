package snownee.fruits.plugin.waila;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.FruitsMod;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

	static final ResourceLocation ITEM = new ResourceLocation("item");
	public static final ResourceLocation BEE = new ResourceLocation(FruitsMod.MODID, "bee");

	@Override
	public void register(IRegistrar registrar) {
		registrar.registerComponentProvider(BeePollenProvider.INSTANCE, TooltipPosition.BODY, Bee.class);
		registrar.registerEntityDataProvider(BeePollenProvider.INSTANCE, Bee.class);
		registrar.addConfig(BEE, true);
	}
}
