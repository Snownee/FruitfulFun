package snownee.fruits.food;

import java.util.function.Consumer;

import com.mojang.serialization.MapCodec;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import snownee.kiwi.loader.Platform;

public class ClearHarmfulEffectsConsumeEffect implements ConsumeEffect, TooltipProvider {
	public static final ClearHarmfulEffectsConsumeEffect INSTANCE = new ClearHarmfulEffectsConsumeEffect();
	public static final MapCodec<ClearHarmfulEffectsConsumeEffect> CODEC = MapCodec.unit(() -> INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ClearHarmfulEffectsConsumeEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<ClearHarmfulEffectsConsumeEffect> getType() {
		return FoodModule.CLEAR_HARMFUL_EFFECTS.get();
	}

	@Override
	public boolean apply(Level level, ItemStack stack, LivingEntity user) {
		ItemStack milk = Items.MILK_BUCKET.getDefaultInstance();
		for (Holder<MobEffect> holder : user.getActiveEffects().stream()
				.filter(effect -> Platform.isCurativeItem(effect, milk))
				.map(MobEffectInstance::getEffect)
				.filter(effect -> effect.value().isBeneficial())
				.toList()) {
			user.removeEffect(holder);
		}
		return true;
	}

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		consumer.accept(Component.translatable("tip.fruitfulfun.clearHarmfulEffects").withStyle(ChatFormatting.BLUE));
	}
}
