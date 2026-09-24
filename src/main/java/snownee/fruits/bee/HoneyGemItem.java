package snownee.fruits.bee;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import snownee.kiwi.item.ModItem;

public class HoneyGemItem extends ModItem {
	public HoneyGemItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (!(entity instanceof Bee bee)) {
			return InteractionResult.PASS;
		}
		BeeAttributes attributes = BeeAttributes.of(bee);
		if (!attributes.getTrusted().isEmpty()) {
			return InteractionResult.FAIL;
		}
		if (player.level() instanceof ServerLevel level) {
			attributes.addTrusted(player.getUUID());
			stack.shrink(1);
			player.awardStat(Stats.ITEM_USED.get(this));
			level.sendParticles(ParticleTypes.HEART, bee.getX(), bee.getY() + bee.getBbHeight() * 0.5, bee.getZ(), 4, 0.3, 0.2, 0.3, 0);
			level.playSound(null, bee, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1, 1.4F);
			return InteractionResult.SUCCESS_SERVER;
		}
		return InteractionResult.CONSUME;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(
			ItemStack itemStack,
			TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> builder,
			TooltipFlag tooltipFlag) {
		builder.accept(Component.empty());
		builder.accept(Component.translatable("tip.fruitfulfun.whenUseOnBee").withStyle(ChatFormatting.GRAY));
		builder.accept(Component.translatable("tip.fruitfulfun.honeyGemUse").withStyle(ChatFormatting.BLUE));
	}
}