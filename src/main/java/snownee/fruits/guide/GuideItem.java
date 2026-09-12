package snownee.fruits.guide;

import com.klikli_dev.modonomicon.item.ModonomiconCustomItemBase;

import net.minecraft.world.item.Item;
import snownee.fruits.FruitfulFun;

public class GuideItem extends ModonomiconCustomItemBase {

	public GuideItem(Item.Properties properties) {
		super(FruitfulFun.id("guide"), properties.stacksTo(1));
	}
}