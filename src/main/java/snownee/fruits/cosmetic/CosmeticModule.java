package snownee.fruits.cosmetic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import snownee.fruits.Hooks;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cosmetic.item.FlowerCrownItem;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.item.ModItem;

/**
 * Optional module holding every head-worn cosmetic: the cherry flower crowns and
 * the beekeeping hats. It depends on the cherry module for the crown petal
 * particles / leaves and on the bee module for the bee the hats are worn on.
 */
@KiwiModule(value = "cosmetic", modId = "fruitfulfun", dependencies = "@cherry")
@KiwiModule.Optional
public class CosmeticModule extends AbstractModule {

	@Category(value = Categories.INGREDIENTS, after = "turtle_helmet")
	public static final ItemObject<FlowerCrownItem> CHERRY_CROWN = flowerCrown(CherryModule.PETAL_CHERRY);
	public static final ItemObject<FlowerCrownItem> REDLOVE_CROWN = flowerCrown(CherryModule.PETAL_REDLOVE);
	@Category(value = Categories.INGREDIENTS, after = "turtle_helmet")
	public static final ItemObject<Item> MUSHROOM_HAT = hat();
	public static final ItemObject<Item> WITCH_HAT = hat();
	public static final ItemObject<Item> SHARK_HAT = hat();
	public static final ItemObject<Item> STRAW_HAT = hat();
	public static final TagKey<Item> HEAD_COSMETIC = itemTag("fruitfulfun", "head_cosmetic");

	public CosmeticModule() {
		Hooks.cosmetic = true;
	}

	public static ItemObject<FlowerCrownItem> flowerCrown(KiwiGO<SimpleParticleType> particle) {
		return item($ -> new FlowerCrownItem(
				$.delayedComponent(
						DataComponents.EQUIPPABLE,
						_ -> Equippable.builder(EquipmentSlot.HEAD).setEquipSound(CherryModule.EQUIP_CROWN.holderOrThrow()).build()),
				particle.getOrCreate()));
	}

	public static ItemObject<Item> hat() {
		return item($ -> new ModItem($.stacksTo(1).delayedComponent(
				DataComponents.EQUIPPABLE,
				_ -> Equippable.builder(EquipmentSlot.HEAD).setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER).build())));
	}
}
