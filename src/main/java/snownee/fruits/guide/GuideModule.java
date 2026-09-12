package snownee.fruits.guide;

import net.minecraft.world.item.Item;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "guide", modId = FruitfulFun.ID, dependencies = "modonomicon")
@KiwiModule.Optional
public class GuideModule extends AbstractModule {

	@Category(value = Categories.TOOLS_AND_UTILITIES)
	public static final ItemObject<Item> GUIDE = item(GuideItem::new);

	public GuideModule() {
		Hooks.guide = true;
	}
}