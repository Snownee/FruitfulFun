package snownee.fruits.util;

import java.util.function.Consumer;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import snownee.fruits.CoreModule;

public class ExtinguishFireConsumeEffect implements ConsumeEffect, TooltipProvider {
	public static final ExtinguishFireConsumeEffect INSTANCE = new ExtinguishFireConsumeEffect();
	public static final MapCodec<ExtinguishFireConsumeEffect> CODEC = MapCodec.unit(() -> INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExtinguishFireConsumeEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<ExtinguishFireConsumeEffect> getType() {
		return CoreModule.EXTINGUISH_FIRE.get();
	}

	@Override
	public boolean apply(Level level, ItemStack stack, LivingEntity user) {
		user.extinguishFire();
		return true;
	}

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		consumer.accept(Component.translatable("tip.fruitfulfun.extinguishFire").withStyle(net.minecraft.ChatFormatting.BLUE));
	}
}
