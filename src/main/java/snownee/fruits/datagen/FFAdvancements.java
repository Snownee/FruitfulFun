package snownee.fruits.datagen;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PickedUpItemTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;

public class FFAdvancements extends FabricAdvancementProvider {

	protected FFAdvancements(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateAdvancement(Consumer<Advancement> consumer) {
		Advancement rootDummy = AdvancementSubProvider.createPlaceholder("husbandry/root");
		AdvancementRewards xp100 = AdvancementRewards.Builder.experience(100).build();

		// use `registerAdvancement` to disable mojang from sending telemetry messages
		Advancement start = Advancement.Builder.recipeAdvancement()
				.parent(rootDummy)
				.display(
						CoreModule.LEMON.get(),
						Component.translatable("advancements.fruitfulfun.start.title"),
						Component.translatable("advancements.fruitfulfun.start.description"),
						null, FrameType.TASK, false, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(FFItemTagsProvider.FRUITS).build()))
				.save(consumer, "husbandry/fruitfulfun/start");

		Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						CoreModule.GRAPEFRUIT.get(),
						Component.translatable("advancements.fruitfulfun.grapefruit.title"),
						Component.translatable("advancements.fruitfulfun.grapefruit.description"),
						null, FrameType.GOAL, true, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CoreModule.GRAPEFRUIT_SAPLING.get()))
				.rewards(xp100)
				.save(consumer, "husbandry/fruitfulfun/grapefruit");

		Advancement apple = Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						CoreModule.APPLE_SAPLING.get(),
						Component.translatable("advancements.fruitfulfun.apple.title"),
						Component.translatable("advancements.fruitfulfun.apple.description"),
						null, FrameType.TASK, true, true, false)
				.addCriterion("_", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(
						ContextAwarePredicate.ANY,
						ItemPredicate.Builder.item().of(CoreModule.APPLE_SAPLING.get()).build(),
						EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.VILLAGER).flags(
								EntityFlagsPredicate.Builder.flags().setIsBaby(Boolean.TRUE).build()
						).build())))
				.save(consumer, "husbandry/fruitfulfun/apple");

		Advancement.Builder.recipeAdvancement()
				.parent(apple)
				.display(
						CherryModule.REDLOVE.get(),
						Component.translatable("advancements.fruitfulfun.redlove.title"),
						Component.translatable("advancements.fruitfulfun.redlove.description"),
						null, FrameType.GOAL, true, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CherryModule.REDLOVE_SAPLING.get()))
				.rewards(xp100)
				.save(consumer, "husbandry/fruitfulfun/redlove");

		Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						Items.FEATHER,
						Component.translatable("advancements.fruitfulfun.bee_jockey.title"),
						Component.translatable("advancements.fruitfulfun.bee_jockey.description"),
						null, FrameType.GOAL, true, true, false)
				.addCriterion("_", StartRidingTrigger.TriggerInstance.playerStartsRiding(
						EntityPredicate.Builder.entity().vehicle(
								EntityPredicate.Builder.entity().of(EntityType.BEE).build()
						)
				))
				.rewards(xp100)
				.save(consumer, "husbandry/fruitfulfun/bee_jockey");

		Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						Items.GOAT_HORN,
						Component.translatable("advancements.fruitfulfun.horn.title"),
						Component.translatable("advancements.fruitfulfun.horn.description"),
						null, FrameType.TASK, true, true, false)
				.addCriterion("_", new ImpossibleTrigger.TriggerInstance())
				.save(consumer, "husbandry/fruitfulfun/horn");
		/* on */
	}

}
