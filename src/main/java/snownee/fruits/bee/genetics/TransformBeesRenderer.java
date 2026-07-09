package snownee.fruits.bee.genetics;

import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import snownee.lychee.util.action.ItemBasedActionRenderer;

public class TransformBeesRenderer implements ItemBasedActionRenderer<TransformBees> {
	@Override
	public ItemStackTemplate getItem(TransformBees transformBees) {
		return new ItemStackTemplate(Items.BEE_SPAWN_EGG);
	}

	@Override
	public List<Component> getBaseTooltips(TransformBees action, @Nullable Player player) {
		List<Component> baseTooltips = ItemBasedActionRenderer.super.getBaseTooltips(action, player);
		if (!action.addTraits().isEmpty()) {
			baseTooltips.add(Component.literal("+: ")
					.append(ComponentUtils.formatList(
							action.addTraits().stream().map(Trait::displayName).toList(),
							ComponentUtils.DEFAULT_SEPARATOR)));
		}
		if (!action.removeTraits().isEmpty()) {
			baseTooltips.add(Component.literal("-: ")
					.append(ComponentUtils.formatList(
							action.removeTraits().stream().map(Trait::displayName).toList(),
							ComponentUtils.DEFAULT_SEPARATOR)));
		}
		return baseTooltips;
	}
}
