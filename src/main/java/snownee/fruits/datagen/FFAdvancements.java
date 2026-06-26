package snownee.fruits.datagen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.PickedUpItemTrigger;
import net.minecraft.advancements.criterion.StartRidingTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.food.FoodModule;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.recipe.ModuleLoadedCondition;
import snownee.kiwi.util.GameObjectLookup;

public class FFAdvancements extends FabricAdvancementProvider {

	protected FFAdvancements(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
		HolderLookup<EntityType<?>> entityTypes = registryLookup.lookupOrThrow(Registries.ENTITY_TYPE);
		HolderLookup.RegistryLookup<Item> items = registryLookup.lookupOrThrow(Registries.ITEM);

		AdvancementHolder rootDummy = AdvancementSubProvider.createPlaceholder("husbandry/root");
		AdvancementRewards xp100 = AdvancementRewards.Builder.experience(100).build();

		// use `registerAdvancement` to disable mojang from sending telemetry messages
		AdvancementHolder start = Advancement.Builder.recipeAdvancement()
				.parent(rootDummy)
				.display(
						CoreModule.LEMON.get(),
						Component.translatable("advancements.fruitfulfun.start.title"),
						Component.translatable("advancements.fruitfulfun.start.description"),
						null, AdvancementType.TASK, false, true, false)
				.addCriterion(
						"_",
						InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
								.of(items, FFItemTagsProvider.FRUITS)
								.build()))
				.save(consumer, "husbandry/fruitfulfun/start");

		AdvancementHolder grapefruit = Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						CoreModule.GRAPEFRUIT.get(),
						Component.translatable("advancements.fruitfulfun.grapefruit.title"),
						Component.translatable("advancements.fruitfulfun.grapefruit.description"),
						null, AdvancementType.GOAL, true, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CoreModule.GRAPEFRUIT_SAPLING.get()))
				.rewards(xp100)
				.save(consumer, "husbandry/fruitfulfun/grapefruit");

		AdvancementHolder apple = Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						CoreModule.APPLE_SAPLING.get(),
						Component.translatable("advancements.fruitfulfun.apple.title"),
						Component.translatable("advancements.fruitfulfun.apple.description"),
						null, AdvancementType.TASK, true, true, false)
				.addCriterion(
						"_", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(
								Optional.empty(),
								Optional.of(ItemPredicate.Builder.item().of(items, CoreModule.APPLE_SAPLING.get()).build()),
								Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity()
										.of(entityTypes, EntityType.VILLAGER)
										.flags(EntityFlagsPredicate.Builder.flags().setIsBaby(Boolean.TRUE))))))
				.save(consumer, "husbandry/fruitfulfun/apple");

		Advancement.Builder.recipeAdvancement()
				.parent(apple)
				.display(
						CherryModule.REDLOVE.get(),
						Component.translatable("advancements.fruitfulfun.redlove.title"),
						Component.translatable("advancements.fruitfulfun.redlove.description"),
						null, AdvancementType.GOAL, true, true, false)
				.addCriterion("_", InventoryChangeTrigger.TriggerInstance.hasItems(CherryModule.REDLOVE_SAPLING.get()))
				.rewards(xp100)
				.save(consumer, "husbandry/fruitfulfun/redlove");

		Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						Items.GOAT_HORN,
						Component.translatable("advancements.fruitfulfun.horn.title"),
						Component.translatable("advancements.fruitfulfun.horn.description"),
						null, AdvancementType.TASK, true, true, false)
				.addCriterion("_", impossible())
				.save(consumer, "husbandry/fruitfulfun/horn");

		Consumer<AdvancementHolder> beeExporter = withConditions(
				consumer, new ModuleLoadedCondition(FruitfulFun.id("bee")));

		AdvancementHolder inspector = Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						BeeModule.INSPECTOR.get(),
						Component.translatable("advancements.fruitfulfun.inspector.title"),
						Component.translatable("advancements.fruitfulfun.inspector.description"),
						null, AdvancementType.TASK, true, true, false)
				.addCriterion("_", impossible())
				.save(beeExporter, "husbandry/fruitfulfun/inspector");

		Advancement.Builder.recipeAdvancement()
				.parent(inspector)
				.display(
						Items.FEATHER,
						Component.translatable("advancements.fruitfulfun.bee_jockey.title"),
						Component.translatable("advancements.fruitfulfun.bee_jockey.description"),
						null, AdvancementType.GOAL, true, true, false)
				.addCriterion(
						"_",
						StartRidingTrigger.TriggerInstance.playerStartsRiding(EntityPredicate.Builder.entity()
								.vehicle(EntityPredicate.Builder.entity().of(entityTypes, EntityType.BEE))
						))
				.rewards(xp100)
				.save(beeExporter, "husbandry/fruitfulfun/bee_jockey");

		Advancement.Builder.recipeAdvancement()
				.parent(inspector)
				.display(
						Items.BEEHIVE,
						Component.translatable("advancements.fruitfulfun.apiarist.title"),
						Component.translatable("advancements.fruitfulfun.apiarist.description"),
						null, AdvancementType.CHALLENGE, true, true, false)
				.addCriterion("_", impossible())
				.save(beeExporter, "husbandry/fruitfulfun/apiarist");

		Consumer<AdvancementHolder> ritualExporter = withConditions(
				consumer, new ModuleLoadedCondition(FruitfulFun.id("ritual")));

		AdvancementHolder ritual = Advancement.Builder.recipeAdvancement()
				.parent(start)
				.display(
						FoodModule.CHORUS_FRUIT_PIE.get(),
						Component.translatable("advancements.fruitfulfun.ritual.title"),
						Component.translatable("advancements.fruitfulfun.ritual.description"),
						null, AdvancementType.TASK, true, true, false)
				.addCriterion("_", impossible())
				.save(ritualExporter, "husbandry/fruitfulfun/ritual");

		AdvancementHolder hauntingInteraction = Advancement.Builder.recipeAdvancement()
				.parent(ritual)
				.display(
						Items.BELL,
						Component.translatable("advancements.fruitfulfun.haunting_interaction.title"),
						Component.translatable("advancements.fruitfulfun.haunting_interaction.description"),
						null, AdvancementType.TASK, true, true, false)
				.addCriterion("_", impossible())
				.save(ritualExporter, "husbandry/fruitfulfun/haunting_interaction");

		Advancement.Builder.recipeAdvancement()
				.parent(hauntingInteraction)
				.display(
						Items.IRON_AXE,
						Component.translatable("advancements.fruitfulfun.haunting_skill.title"),
						Component.translatable("advancements.fruitfulfun.haunting_skill.description"),
						null, AdvancementType.CHALLENGE, true, true, false)
				.addCriterion("_", impossible())
				.save(ritualExporter, "husbandry/fruitfulfun/haunting_skill");

		Consumer<AdvancementHolder> foodExporter = withConditions(
				consumer, new ModuleLoadedCondition(FruitfulFun.id("food")));
		BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup).forEach(DataComponentInitializers.PendingComponents::apply);
		Item[] foods = GameObjectLookup.all(BuiltInRegistries.ITEM, FruitfulFun.ID)
				.filter($ -> $.components().has(DataComponents.FOOD))
				.filter(Predicate.not(FoodModule.RICE_WITH_FRUITS.get().asItem()::equals))
				.toArray(Item[]::new);
		addFood(items, Advancement.Builder.recipeAdvancement(), foods)
				.parent(grapefruit)
				.display(
						PomegranateModule.POMEGRANATE.get(),
						Component.translatable("advancements.fruitfulfun.all_fruit.title"),
						Component.translatable("advancements.fruitfulfun.all_fruit_and_food.description"),
						null, AdvancementType.CHALLENGE, true, true, false)
				.rewards(xp100)
				.save(foodExporter, "husbandry/fruitfulfun/all_fruit_and_food");

		Consumer<AdvancementHolder> noFoodExporter = withConditions(
				consumer, ResourceConditions.not(new ModuleLoadedCondition(FruitfulFun.id("food"))));
		foods = FFRegistries.FRUIT_TYPE.stream()
				.map($ -> $.fruit.get())
				.filter($ -> $.components().has(DataComponents.FOOD))
				.toArray(Item[]::new);
		addFood(items, Advancement.Builder.recipeAdvancement(), foods)
				.parent(grapefruit)
				.display(
						PomegranateModule.POMEGRANATE.get(),
						Component.translatable("advancements.fruitfulfun.all_fruit.title"),
						Component.translatable("advancements.fruitfulfun.all_fruit.description"),
						null, AdvancementType.CHALLENGE, true, true, false)
				.rewards(xp100)
				.save(noFoodExporter, "husbandry/fruitfulfun/all_fruit");
		/* on */
	}

	private static Advancement.Builder addFood(HolderLookup.RegistryLookup<Item> lookup, Advancement.Builder builder, Item[] items) {
		for (Item item : items) {
			builder.addCriterion(
					BuiltInRegistries.ITEM.getKey(item).getPath(),
					ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(lookup, item)));
		}
		return builder;
	}

	private static Criterion<ImpossibleTrigger.TriggerInstance> impossible() {
		return CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());
	}
}
