package snownee.fruits.compat.jade;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.block.entity.SlidingDoorEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.kiwi.loader.Platform;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {

	public static final ResourceLocation BEE = new ResourceLocation(FruitfulFun.ID, "bee");
	public static final ResourceLocation CROP_PROGRESS = new ResourceLocation(FruitfulFun.ID, "crop_progress");

	@Override
	public void register(IWailaCommonRegistration registration) {
		if (!Platform.isProduction()) {
			registration.registerBlockDataProvider(new FruitLeavesDebugProvider(), FruitTreeBlockEntity.class);
			registration.registerEntityDataProvider(new BeeDebugProvider(), Bee.class);
		}
		registration.registerEntityDataProvider(BeePollenProvider.INSTANCE, Bee.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		if (!Platform.isProduction()) {
			registration.registerBlockComponent(new FruitLeavesDebugProvider(), FruitLeavesBlock.class);
			registration.registerEntityComponent(new BeeDebugProvider(), Bee.class);
		}
		registration.registerBlockComponent(CropProgressProvider.INSTANCE, FruitLeavesBlock.class);
		registration.registerEntityComponent(BeePollenProvider.INSTANCE, Bee.class);
		registration.addRayTraceCallback((hit, accessor, original) -> override(original, registration));
	}

	private static @Nullable Accessor<?> override(@Nullable Accessor<?> accessor, IWailaClientRegistration registration) {
		if (accessor instanceof EntityAccessor) {
			Entity entity = ((EntityAccessor) accessor).getEntity();
			if (entity instanceof SlidingDoorEntity) {
				BlockPos pos = entity.blockPosition();
				Level level = accessor.getLevel();
				BlockHitResult hitResult = new BlockHitResult(accessor.getHitResult().getLocation(), accessor.getPlayer().getDirection().getOpposite(), pos, false);
				return registration.blockAccessor().blockState(level.getBlockState(pos)).level(level).player(accessor.getPlayer()).hit(hitResult).build();
			}
		}
		return accessor;
	}
}
