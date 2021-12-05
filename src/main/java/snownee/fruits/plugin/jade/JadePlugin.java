package snownee.fruits.plugin.jade;

import mcp.mobius.waila.api.Accessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaRayTraceEvent;
import mcp.mobius.waila.impl.BlockAccessorImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import snownee.fruits.FruitsMod;
import snownee.fruits.cherry.block.SlidingDoorEntity;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

	static final ResourceLocation ITEM = new ResourceLocation("item");
	public static final ResourceLocation BEE = new ResourceLocation(FruitsMod.MODID, "bee");

	public JadePlugin() {
		MinecraftForge.EVENT_BUS.addListener(this::override);
	}

	@Override
	public void register(IRegistrar registrar) {
		registrar.registerComponentProvider(BeePollenProvider.INSTANCE, TooltipPosition.BODY, Bee.class);
		registrar.registerEntityDataProvider(BeePollenProvider.INSTANCE, Bee.class);
		registrar.addConfig(BEE, true);
	}

	private void override(WailaRayTraceEvent event) {
		Accessor<?> accessor = event.getAccessor();
		if (accessor instanceof EntityAccessor) {
			Entity entity = ((EntityAccessor) accessor).getEntity();
			if (entity instanceof SlidingDoorEntity) {
				BlockPos pos = ((SlidingDoorEntity) entity).doorPos;
				Level level = accessor.getLevel();
				BlockHitResult hitResult = new BlockHitResult(accessor.getHitResult().getLocation(), accessor.getPlayer().getDirection().getOpposite(), pos, false);
				accessor = new BlockAccessorImpl(level.getBlockState(pos), null, level, accessor.getPlayer(), accessor.getServerData(), hitResult, accessor.isServerConnected());
				event.setAccessor(accessor);
			}
		}
	}
}
