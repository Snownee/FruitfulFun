package snownee.fruits.mixin.brewer;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBrewingStand;
import snownee.fruits.gadget.GadgetModule;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin implements FFBrewingStand {
	@Mutable
	@Shadow
	@Final
	protected ContainerData dataAccess;
	@Shadow
	private int brewTime;
	@Unique
	private ContainerData oldDataAccess;
	@Unique
	private int brewSpeedBonus;
	@Unique
	private int[] lastRecipeHash = new int[3];

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(BlockPos worldPosition, BlockState blockState, CallbackInfo ci) {
		oldDataAccess = dataAccess;
		if (!Hooks.gadget || !GadgetModule.BREWER.is(blockState)) {
			return;
		}
		dataAccess = new ContainerData() {
			@Override
			public int get(int dataId) {
				return dataId == oldDataAccess.getCount() ? brewSpeedBonus : oldDataAccess.get(dataId);
			}

			@Override
			public void set(int dataId, int value) {
				if (dataId == oldDataAccess.getCount()) {
					brewSpeedBonus = value;
				} else {
					oldDataAccess.set(dataId, value);
				}
			}

			@Override
			public int getCount() {
				return oldDataAccess.getCount() + 1;
			}
		};
	}

	@WrapOperation(
			method = "serverTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;doBrew(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
	private static void doBrew(
			Level level,
			BlockPos pos,
			NonNullList<ItemStack> items,
			Operation<Void> original) {
		if (GadgetModule.BREWER.is(level.getBlockState(pos))) {
			GadgetModule.doBrew(level, pos, items, (BrewingStandBlockEntity) level.getBlockEntity(pos));
		} else {
			original.call(level, pos, items);
		}
	}

	@Inject(
			method = "serverTick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
	private static void applySpeedBonus(
			Level level, BlockPos pos, BlockState selfState, BrewingStandBlockEntity entity, CallbackInfo ci) {
		BrewingStandBlockEntityMixin mixin = (BrewingStandBlockEntityMixin) (Object) entity;
		if (mixin.brewSpeedBonus > 0 && mixin.brewTime > 0) {
			mixin.brewTime = (int) (mixin.brewTime / (1 + FFBrewingStand.calculateBrewSpeedBonus(mixin.brewSpeedBonus)));
		}
	}

	@Inject(method = "getDefaultName", at = @At("RETURN"), cancellable = true)
	private void getDefaultName(CallbackInfoReturnable<Component> cir) {
		if (fruits$isBrewer()) {
			cir.setReturnValue(FFBrewingStand.DEFAULT_NAME);
		}
	}

	@WrapOperation(method = "createMenu", at = @At(value = "NEW", target = "Lnet/minecraft/world/inventory/BrewingStandMenu;"))
	private BrewingStandMenu createMenu(
			int containerId,
			Inventory inventory,
			Container brewingStand,
			ContainerData brewingStandData,
			Operation<BrewingStandMenu> original) {
		return original.call(containerId, inventory, brewingStand, oldDataAccess);
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void load(CompoundTag tag, CallbackInfo ci) {
		if (fruits$isBrewer()) {
			brewSpeedBonus = tag.getShort("BrewSpeedBonus");
			if (tag.contains("LastRecipeHash", Tag.TAG_INT_ARRAY)) {
				lastRecipeHash = tag.getIntArray("LastRecipeHash");
				Preconditions.checkArgument(lastRecipeHash.length == 3, "LastRecipeHash must be an array of length 3");
			}
		}
	}

	@Inject(method = "saveAdditional", at = @At("RETURN"))
	private void saveAdditional(CompoundTag tag, CallbackInfo ci) {
		if (fruits$isBrewer()) {
			tag.putShort("BrewSpeedBonus", (short) brewSpeedBonus);
			tag.putIntArray("LastRecipeHash", lastRecipeHash);
		}
	}

	@Override
	public boolean fruits$isBrewer() {
		return Hooks.gadget && GadgetModule.BREWER.is(((BrewingStandBlockEntity) (Object) this).getBlockState());
	}

	@Override
	public ContainerData fruits$dataAccess() {
		return dataAccess;
	}

	@Override
	public void fruits$updateRecipeHash(int[] recipeHash) {
		int sameCount = 0;
		for (int i = 0; i < 3; i++) {
			int last = lastRecipeHash[i];
			int now = recipeHash[i];
			if (last == now && now != 0) {
				sameCount++;
			}
			lastRecipeHash[i] = now;
		}
		brewSpeedBonus = sameCount == 0 ? 0 : Math.min(brewSpeedBonus + sameCount, FFCommonConfig.brewerMaxSpeedRequirement);
	}
}
