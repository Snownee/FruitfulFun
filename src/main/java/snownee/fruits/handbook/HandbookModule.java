package snownee.fruits.handbook;

import net.minecraft.world.item.Item;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "handbook", modId = FruitfulFun.ID, dependencies = "modonomicon")
@KiwiModule.Optional
public class HandbookModule extends AbstractModule {

	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "written_book")
	public static final ItemObject<Item> HANDBOOK = item(HandbookItem::new);

	public HandbookModule() {
		Hooks.handbook = true;
	}
}