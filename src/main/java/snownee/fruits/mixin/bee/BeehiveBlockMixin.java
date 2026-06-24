package snownee.fruits.mixin.bee;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
	@ModifyExpressionValue(
			method = "angerNearbyBees",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/animal/bee/Bee;getTarget()Lnet/minecraft/world/entity/LivingEntity;"))
	private @Nullable LivingEntity angerNearbyBees(@Nullable LivingEntity original, @Local(name = "bee") Bee bee) {
		if (original == null && BeeAttributes.of(bee).hasTrait(Trait.MILD)) {
			return bee; // return anything nonnull to continue the loop
		}
		return original;
	}

	@WrapOperation(
			method = "playerDestroy", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;hasTag(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/tags/TagKey;)Z"))
	private boolean playerDestroy(
			ItemStack item,
			TagKey<Enchantment> tag,
			Operation<Boolean> original,
			@Local(name = "beehiveBlockEntity") BeehiveBlockEntity beehiveBlockEntity) {
		boolean result = original.call(item, tag);
		if (!result && ((FFBeehiveBlockEntity) beehiveBlockEntity).fruits$isWaxed()) {
			((FFBeehiveBlockEntity) beehiveBlockEntity).fruits$setWaxed(false);
			return true;
		}
		return result;
	}

	@Inject(method = "getDrops", at = @At("HEAD"))
	private void getDrops(BlockState state, LootParams.Builder params, CallbackInfoReturnable<List<ItemStack>> cir) {
		if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FFBeehiveBlockEntity be && be.fruits$isWaxed()) {
			// EnderMan.dropCustomDeathLoot
			ItemStack fakeTool = Items.DIAMOND_AXE.getDefaultInstance();
			fakeTool.enchant(params.getLevel().registryAccess().getOrThrow(Enchantments.SILK_TOUCH), 1);
			params.withParameter(LootContextParams.TOOL, fakeTool);
		}
	}

	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void useItemOn(
			ItemStack itemStack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<InteractionResult> cir) {
		if (!(level.getBlockEntity(pos) instanceof BeehiveBlockEntity be)) {
			return;
		}
		boolean bl = false;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		// https://bugs.mojang.com/projects/MC/issues/MC-276925
		// Distance limit of using lead on a fence is directional
		List<Bee> bees = level.getEntitiesOfClass(Bee.class, new AABB(i - 7, j - 7, k - 7, i + 8, j + 8, k + 8));
		for (Bee bee : bees) {
			if (bee.getLeashHolder() != player) {
				continue;
			}
			if (!level.isClientSide() && !be.isFull()) {
				bee.dropLeash();
				be.addOccupant(bee);
			}
			bl = true;
		}
		if (!bl) {
			return;
		}
		if (!level.isClientSide()) {
			level.playSound(null, pos, SoundEvents.LEAD_TIED, player.getSoundSource(), 1, 1);
		}
		cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
	}
}
