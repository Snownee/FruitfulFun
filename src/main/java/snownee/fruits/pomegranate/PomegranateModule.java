package snownee.fruits.pomegranate;

import net.minecraft.world.item.Item;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.item.ModItem;
import snownee.lychee.LycheeRegistries;

@KiwiModule("pomegranate")
public class PomegranateModule extends AbstractModule {
	public static final KiwiGO<FFExplodeAction.Type> EXPLODE = go(FFExplodeAction.Type::new, () -> LycheeRegistries.POST_ACTION);
	public static final KiwiGO<Item> POMEGRANATE = go(() -> new ModItem(itemProp()));
}
