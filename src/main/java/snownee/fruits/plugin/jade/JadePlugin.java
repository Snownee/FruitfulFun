package snownee.fruits.plugin.jade;

import mcp.mobius.waila.api.Accessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaCommonRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaRayTraceEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import snownee.fruits.FruitsMod;
import snownee.fruits.cherry.block.SlidingDoorEntity;

@SuppressWarnings("UnstableApiUsage")
@WailaPlugin
public class JadePlugin implements IWailaPlugin {

	public static final ResourceLocation BEE = new ResourceLocation(FruitsMod.MODID, "bee");

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		MinecraftForge.EVENT_BUS.addListener((WailaRayTraceEvent event) -> {
			Accessor<?> accessor = event.getAccessor();
			if (accessor instanceof EntityAccessor) {
				Entity entity = ((EntityAccessor) accessor).getEntity();
				if (entity instanceof SlidingDoorEntity) {
					BlockPos pos = ((SlidingDoorEntity) entity).doorPos;
					Level level = accessor.getLevel();
					BlockHitResult hitResult = new BlockHitResult(accessor.getHitResult().getLocation(),
							accessor.getPlayer().getDirection().getOpposite(),
							pos,
							false);
					accessor = registration.createBlockAccessor(level.getBlockState(pos),
							null,
							level,
							accessor.getPlayer(),
							accessor.getServerData(),
							hitResult,
							accessor.isServerConnected());
					event.setAccessor(accessor);
				}
			}
		});
		registration.registerComponentProvider(BeePollenProvider.INSTANCE, TooltipPosition.BODY, Bee.class);
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerEntityDataProvider(BeePollenProvider.INSTANCE, Bee.class);
		registration.addConfig(BEE, true);
	}
}
