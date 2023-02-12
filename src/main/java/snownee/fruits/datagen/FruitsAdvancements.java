package snownee.fruits.datagen;

import java.util.function.Consumer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PickedUpItemTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;

public class FruitsAdvancements implements Consumer<Consumer<Advancement>> {
	private final ExistingFileHelper fileHelper;

	public FruitsAdvancements(ExistingFileHelper fileHelper) {
		this.fileHelper = fileHelper;
	}

	public void accept(Consumer<Advancement> consumer) {
		AdvancementRewards xp100 = AdvancementRewards.Builder.experience(100).build();

		/* off */
		Advancement start = Advancement.Builder.advancement()
				.parent(new ResourceLocation("husbandry/root"))
				.display(
						CoreModule.LEMON.get(),
						Component.translatable("advancements.fruittrees.start.title"),
						Component.translatable("advancements.fruittrees.start.description"),
						null, FrameType.TASK, false, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(CommonItemTagsProvider.FRUITS).build()))
				.save(consumer, new ResourceLocation("husbandry/fruittrees/start"), fileHelper);

		Advancement grapefruit = Advancement.Builder.advancement()
				.parent(start)
				.display(
						CoreModule.GRAPEFRUIT.get(),
						Component.translatable("advancements.fruittrees.grapefruit.title"),
						Component.translatable("advancements.fruittrees.grapefruit.description"),
						null, FrameType.GOAL, true, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CoreModule.GRAPEFRUIT_SAPLING.get()))
				.rewards(xp100)
				.save(consumer, "husbandry/fruittrees/grapefruit");

		Advancement.Builder.advancement()
				.parent(grapefruit)
				.display(
						CoreModule.EMPOWERED_CITRON.get(),
						Component.translatable("advancements.fruittrees.forestbat.title"),
						Component.translatable("advancements.fruittrees.forestbat.description"),
						null, FrameType.TASK, true, true, true)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CoreModule.EMPOWERED_CITRON.get()))
				.save(consumer, "husbandry/fruittrees/forestbat");

		Advancement apple = Advancement.Builder.advancement()
				.parent(start)
				.display(
						CoreModule.APPLE_SAPLING.get(),
						Component.translatable("advancements.fruittrees.apple.title"),
						Component.translatable("advancements.fruittrees.apple.description"),
						null, FrameType.TASK, true, true, false)
				.addCriterion("_", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(
						EntityPredicate.Composite.ANY,
						ItemPredicate.Builder.item().of(CoreModule.APPLE_SAPLING.get()).build(),
						EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.VILLAGER).flags(
								EntityFlagsPredicate.Builder.flags().setIsBaby(Boolean.TRUE).build()
						).build())))
				.save(consumer, "husbandry/fruittrees/apple");

		Advancement.Builder.advancement()
				.parent(apple)
				.display(
						CherryModule.REDLOVE.get(),
						Component.translatable("advancements.fruittrees.redlove.title"),
						Component.translatable("advancements.fruittrees.redlove.description"),
						null, FrameType.GOAL, true, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CherryModule.REDLOVE_SAPLING.get()))
				.rewards(xp100)
				.save(consumer, "husbandry/fruittrees/redlove");

		Advancement.Builder.advancement()
				.parent(start)
				.display(
						Items.GOAT_HORN,
						Component.translatable("advancements.fruittrees.horn.title"),
						Component.translatable("advancements.fruittrees.horn.description"),
						null, FrameType.TASK, true, true, false)
				.addCriterion("_", new ImpossibleTrigger.TriggerInstance())
				.save(consumer, "husbandry/fruittrees/horn");
		/* on */
	}

}
