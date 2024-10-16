package snownee.fruits.ritual;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.bee.genetics.Trait;
import snownee.lychee.client.core.post.ItemBasedPostActionRenderer;

public class TransformBeesRenderer implements ItemBasedPostActionRenderer<TransformBees> {
	@Override
	public ItemStack getItem(TransformBees transformBees) {
		return Items.BEE_SPAWN_EGG.getDefaultInstance();
	}

	@Override
	public List<Component> getBaseTooltips(TransformBees action) {
		List<Component> baseTooltips = ItemBasedPostActionRenderer.super.getBaseTooltips(action);
		if (!action.addTraits.isEmpty()) {
			baseTooltips.add(Component.literal("+: ")
					.append(ComponentUtils.formatList(
							action.addTraits.stream().map(Trait::getDisplayName).toList(),
							ComponentUtils.DEFAULT_SEPARATOR)));
		}
		if (!action.removeTraits.isEmpty()) {
			baseTooltips.add(Component.literal("-: ")
					.append(ComponentUtils.formatList(
							action.removeTraits.stream().map(Trait::getDisplayName).toList(),
							ComponentUtils.DEFAULT_SEPARATOR)));
		}
		return baseTooltips;
	}
}
