package snownee.fruits.mixin.crafter;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(BlockItemStateProperties.class)
public class BlockItemStatePropertiesMixin {
	@WrapOperation(
			method = "addToTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/component/BlockItemStateProperties;get(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private static <T extends Comparable<T>> @Nullable T hideHoneyLevel(
			BlockItemStateProperties instance,
			Property<T> property,
			Operation<T> original,
			@Local(argsOnly = true, name = "components") DataComponentGetter components) {
		if (Hooks.gadget && components.get(GadgetModule.HIDE_HONEY_LEVEL.get()) != null) {
			return null;
		}
		return original.call(instance, property);
	}
}
