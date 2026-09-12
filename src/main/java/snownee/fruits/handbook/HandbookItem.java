package snownee.fruits.handbook;

import com.klikli_dev.modonomicon.item.ModonomiconCustomItemBase;

import net.minecraft.world.item.Item;
import snownee.fruits.FruitfulFun;

public class HandbookItem extends ModonomiconCustomItemBase {

	public HandbookItem(Item.Properties properties) {
		super(FruitfulFun.id("guide"), properties.stacksTo(1));
	}
}