package snownee.fruits.compat.jade;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import snownee.fruits.FruitsMod;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.cherry.block.SlidingDoorEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.kiwi.loader.Platform;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {

	public static final ResourceLocation BEE = new ResourceLocation(FruitsMod.ID, "bee");
	private static IWailaClientRegistration client;

	@Override
	public void register(IWailaCommonRegistration registration) {
		if (!Platform.isProduction()) {
			registration.registerBlockDataProvider(FruitLeavesProvider.INSTANCE, FruitTreeBlockEntity.class);
		}
		registration.registerEntityDataProvider(BeePollenProvider.INSTANCE, Bee.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		if (!Platform.isProduction()) {
			registration.registerBlockComponent(FruitLeavesProvider.INSTANCE, FruitLeavesBlock.class);
		}
		registration.registerEntityComponent(BeePollenProvider.INSTANCE, Bee.class);
		registration.addRayTraceCallback(this::override);
		client = registration;
	}

	private @Nullable Accessor<?> override(HitResult hit, @Nullable Accessor<?> accessor, @Nullable Accessor<?> original) {
		if (accessor instanceof EntityAccessor) {
			Entity entity = ((EntityAccessor) accessor).getEntity();
			if (entity instanceof SlidingDoorEntity) {
				BlockPos pos = ((SlidingDoorEntity) entity).doorPos;
				Level level = accessor.getLevel();
				BlockHitResult hitResult = new BlockHitResult(accessor.getHitResult().getLocation(), accessor.getPlayer().getDirection().getOpposite(), pos, false);
				return client.blockAccessor().blockState(level.getBlockState(pos)).level(level).player(accessor.getPlayer()).hit(hitResult).build();
			}
		}
		return accessor;
	}
}
