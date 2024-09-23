package snownee.fruits.mixin.bee;

import java.util.List;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
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
					target = "Lnet/minecraft/world/entity/animal/Bee;getTarget()Lnet/minecraft/world/entity/LivingEntity;"))
	private LivingEntity angerNearbyBees(LivingEntity original, @Local Bee bee) {
		if (original == null && BeeAttributes.of(bee).hasTrait(Trait.MILD)) {
			return bee; // return anything nonnull to continue the loop
		}
		return original;
	}

	@WrapOperation(
			method = "playerDestroy", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
	private int playerDestroy(
			Enchantment enchantment,
			ItemStack itemStack,
			Operation<Integer> original,
			@Local BeehiveBlockEntity be) {
		if (((FFBeehiveBlockEntity) be).fruits$isWaxed()) {
			((FFBeehiveBlockEntity) be).fruits$setWaxed(false);
			return Math.max(original.call(enchantment, itemStack), 1);
		}
		return original.call(enchantment, itemStack);
	}

	@Inject(method = "getDrops", at = @At("HEAD"))
	private void getDrops(BlockState blockState, LootParams.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
		if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FFBeehiveBlockEntity be && be.fruits$isWaxed()) {
			// EnderMan.dropCustomDeathLoot
			ItemStack itemStack = builder.getParameter(LootContextParams.TOOL);
			itemStack = itemStack.isEmpty() ? Items.DIAMOND_AXE.getDefaultInstance() : itemStack.copy();
			itemStack.enchant(Enchantments.SILK_TOUCH, 1);
			builder.withParameter(LootContextParams.TOOL, itemStack);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(
			BlockState blockState,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hit,
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
			if (!level.isClientSide && !be.isFull()) {
				bee.dropLeash(true, true);
				be.addOccupant(bee, bee.hasNectar());
			}
			bl = true;
		}
		if (!bl) {
			return;
		}
		if (!level.isClientSide) {
			level.playSound(null, pos, SoundEvents.LEASH_KNOT_PLACE, player.getSoundSource(), 1, 1);
		}
		cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
	}
}
