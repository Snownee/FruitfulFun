package snownee.fruits.vacuum;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.util.KiwiEntityTypeBuilder;

@KiwiModule("vacuum")
@KiwiModule.Optional
public class VacModule extends AbstractModule {
	public static final KiwiGO<Item> VAC_GUN_CASING = go(() -> new ModItem(itemProp().stacksTo(1).rarity(Rarity.RARE)));
	public static final KiwiGO<VacGunItem> VAC_GUN = go(VacGunItem::new);
	public static final TagKey<Block> VCD_PERFORM_USING = blockTag(FruitfulFun.ID, "vcd_perform_using");
	public static final TagKey<Block> VCD_PERFORM_BREAKING = blockTag(FruitfulFun.ID, "vcd_perform_breaking");
	public static final KiwiGO<EntityType<VacItemProjectile>> ITEM_PROJECTILE = go(() -> KiwiEntityTypeBuilder.<VacItemProjectile>create()
			.dimensions(EntityDimensions.scalable(0.25f, 0.25f))
			.trackRangeChunks(4)
			.trackedUpdateRate(10)
			.entityFactory(VacItemProjectile::new)
			.build());

	public VacModule() {
		Hooks.vac = true;
	}

	@Override
	protected void preInit() {
		CommonProxy.initVacModule();
	}
}
