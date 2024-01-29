package snownee.fruits.ritual;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import snownee.fruits.FFCommonConfig;

public class CollectDragonBreathDispenseBehavior extends DefaultDispenseItemBehavior {

	private final DispenseItemBehavior original;

	public CollectDragonBreathDispenseBehavior(DispenseItemBehavior original) {
		this.original = original;
	}

	@Override
	protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
		ServerLevel level = blockSource.getLevel();
		BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
		BlockState blockState = level.getBlockState(blockPos);
		if (blockState.is(BlockTags.BEEHIVES, blockStateBase -> blockStateBase.hasProperty(BeehiveBlock.HONEY_LEVEL) && blockStateBase.getBlock() instanceof BeehiveBlock) && blockState.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
			return original.dispense(blockSource, itemStack);
		}
		if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
			return original.dispense(blockSource, itemStack);
		}
		List<AreaEffectCloud> list = level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(blockPos).inflate(0.2), CollectDragonBreathDispenseBehavior::isValidDragonBreath);
		if (!list.isEmpty()) {
			AreaEffectCloud areaEffectCloud2 = list.get(0);
			areaEffectCloud2.setRadius(areaEffectCloud2.getRadius() - 0.5f);
			itemStack.shrink(1);
			ItemStack dragonBreath = new ItemStack(Items.DRAGON_BREATH);
			if (itemStack.isEmpty()) {
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
				return dragonBreath;
			}
			if (((DispenserBlockEntity) blockSource.getEntity()).addItem(dragonBreath.copy()) < 0) {
				super.dispense(blockSource, dragonBreath.copy());
			}
			return itemStack;
		}
		return original.dispense(blockSource, itemStack);
	}

	public static boolean isValidDragonBreath(AreaEffectCloud cloud) {
		if (cloud == null || !cloud.isAlive()) {
			return false;
		}
		if (FFCommonConfig.fixDragonBreathExploit && cloud.getRadius() <= 0) {
			return false;
		}
		return RitualModule.DUMMY_UUID.equals(cloud.ownerUUID) || cloud.getOwner() instanceof EnderDragon;
	}
}
