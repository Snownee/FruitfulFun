package snownee.fruits.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.kiwi.loader.Platform;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosCompat {
	public static void init() {
		ICurioItem curioItem = new ICurioItem() {
		};
		CuriosApi.registerCurio(CherryModule.CHERRY_CROWN.getOrCreate(), curioItem);
		CuriosApi.registerCurio(CherryModule.REDLOVE_CROWN.getOrCreate(), curioItem);

		if (Platform.isPhysicalClient()) {
			CuriosRendererRegistry.register(CherryModule.CHERRY_CROWN.getOrCreate(), FlowerCrownRenderer::new);
			CuriosRendererRegistry.register(CherryModule.REDLOVE_CROWN.getOrCreate(), FlowerCrownRenderer::new);
		}
	}

	public static FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		return (FlowerCrownItem) CuriosApi.getCuriosInventory(entity).resolve()
				.flatMap($ -> $.findFirstCurio(stack -> stack.getItem() instanceof FlowerCrownItem))
				.map(SlotResult::stack)
				.map(ItemStack::getItem).orElse(null);
	}
}
