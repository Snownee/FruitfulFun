package snownee.fruits.pomegranate.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.fruits.pomegranate.PomegranateModule;

public class EnchantedPomegranateItem extends Item {

	public EnchantedPomegranateItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		return PomegranateModule.POMEGRANATE_ITEM.get().use(level, player, interactionHand);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
