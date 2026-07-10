package snownee.fruits.compat.jade;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.block.entity.SlidingDoorEntity;
import snownee.fruits.compat.supplementaries.SupplementariesJadeCompat;
import snownee.fruits.gadget.BuzzyCrafterBlock;
import snownee.fruits.gadget.ScentedCandleBlock;
import snownee.fruits.gadget.ScentedCandleBlockEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IWailaConfig;
import snownee.kiwi.loader.Platform;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {

	public static final Identifier INSPECTOR = FruitfulFun.id("inspector");
	public static final Identifier INSPECTOR_BLOCK = FruitfulFun.id("inspector_block");
	public static final Identifier CROP_PROGRESS = FruitfulFun.id("crop_progress");
	public static final Identifier WAXED = FruitfulFun.id("waxed");
	public static final Identifier CRAFTER = FruitfulFun.id("crafter");

	public static void ensureVisibility(boolean fromEntity) {
		IWailaConfig.General config = IWailaConfig.get().general();
		config.setDisplayTooltip(true);
		if (fromEntity) {
			config.setDisplayEntities(true);
		} else {
			config.setDisplayBlocks(true);
		}
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
		if (!Platform.isProduction()) {
			registration.registerBlockDataProvider(new FruitLeavesDebugProvider(), FruitTreeBlockEntity.class);
			registration.registerEntityDataProvider(new BeeDebugProvider(), Bee.class);
			registration.registerBlockDataProvider(new ScentedCandleDebugProvider(), ScentedCandleBlockEntity.class);
		}
		if (Hooks.bee) {
			registration.registerBlockDataProvider(new BeehiveWaxProvider(), BeehiveBlockEntity.class);
			registration.registerEntityDataProvider(new InspectorProvider(), Bee.class);
			if (Hooks.supplementaries) {
				SupplementariesJadeCompat.register(registration);
			}
		}
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		if (!Platform.isProduction()) {
			registration.registerBlockComponent(new FruitLeavesDebugProvider.Client(), FruitLeavesBlock.class);
			registration.registerEntityComponent(new BeeDebugProvider.Client(), Bee.class);
			registration.registerBlockComponent(new ScentedCandleDebugProvider.Client(), ScentedCandleBlock.class);
		}
		registration.registerBlockComponent(new CropProgressProvider(), FruitLeavesBlock.class);
		registration.addRayTraceCallback((hit, accessor, original) -> override(original, registration));
		if (Hooks.bee) {
			registration.registerEntityComponent(new InspectorProvider.EntityComponent(), Bee.class);
			registration.registerBlockIcon(new BeehiveWaxProvider.Client(), BeehiveBlock.class);
			if (Hooks.supplementaries) {
				SupplementariesJadeCompat.registerClient(registration);
			}
		}
		if (Hooks.gadget) {
			registration.registerBlockComponent(new CrafterProvider(), BuzzyCrafterBlock.class);
		}
	}

	private static @Nullable Accessor<?> override(@Nullable Accessor<?> accessor, IWailaClientRegistration registration) {
		if (accessor instanceof EntityAccessor) {
			Entity entity = ((EntityAccessor) accessor).getEntity();
			if (entity instanceof SlidingDoorEntity door) {
				BlockPos pos = door.doorPos();
				Level level = accessor.getLevel();
				BlockHitResult hitResult = new BlockHitResult(
						accessor.getHitResult().getLocation(), accessor.getPlayer().getDirection().getOpposite(), pos, false);
				return registration.blockAccessor()
						.blockState(level.getBlockState(pos))
						.level(level)
						.player(accessor.getPlayer())
						.hit(hitResult)
						.build();
			}
		}
		return accessor;
	}
}
