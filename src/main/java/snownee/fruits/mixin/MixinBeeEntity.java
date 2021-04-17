package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snownee.fruits.FilteredFlyingPathNavigator;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.Hybridization;

@Mixin(BeeEntity.class)
public abstract class MixinBeeEntity extends AnimalEntity {

	public MixinBeeEntity(EntityType<? extends AnimalEntity> type, World world) {
		super(type, world);
	}

	@Inject(at = @At("HEAD"), method = "isFlowers", cancellable = true)
	public void fruits_isFlowers(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (Hybridization.INSTANCE == null) {
			return;
		}
		if (world.isBlockPresent(pos) && world.getBlockState(pos).getBlock() instanceof FruitLeavesBlock) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "createNavigator", cancellable = true)
	protected void fruits_createNavigator(World worldIn, CallbackInfoReturnable<PathNavigator> cir) {
		FilteredFlyingPathNavigator flyingpathnavigator = new FilteredFlyingPathNavigator(this, worldIn);
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		cir.setReturnValue(flyingpathnavigator);
	}

}
