package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import net.minecraft.core.registries.Registries;
import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.recipe.RecipeUtil;

public class FruitTreeCategory extends IndexModeCategoryProvider {

	public FruitTreeCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new GettingNewTreesEntry(this).generate());
		add(new PlantingEntry(this).generate());
		add(new HybridizingEntry(this).generate());
		add(new FruitUtilitiesEntry(this).generate());
	}

	@Override
	protected String categoryName() {
		return "果树";
	}

	@Override
	protected String categoryDescription() {
		return lines("从种植到杂交，一步步培育出所有果树。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(CoreModule.ORANGE_SAPLING);
	}

	@Override
	public String categoryId() {
		return "fruit_tree";
	}

	public static class GettingNewTreesEntry extends IndexModeEntryProvider {

		public GettingNewTreesEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("寻找果树");
			pageText(lines("""
					在世界中（主要是温暖的森林），你可能会找到零散分布的香橼树、橘子树和酸橙树。它们的外观大多与橡树差不多，但树叶之间有独特的花朵和果实。这些基础品种是日后培育稀有水果时不可或缺的材料。
					<#if cfg:fruitfulfun.common.wanderingTraderSaplingPrice >= 1>
					
					如果觉得出远门太辛苦，也可以从流浪商人处购买这些树苗。
					<#endif>
					"""));

			page("apple", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("村庄里的珍宝");
			pageText(lines("""
					<#if cfg:fruitfulfun.common.appleSaplingFromHeroOfTheVillage && cfg:fruitfulfun.common.villageAppleTreeWorldGen>
					苹果树苗的来源有点特别。当你成为村庄里的英雄后，小村民会将苹果树苗送给你。另外，它也偶尔在平原村庄的中心出现。
					<#elif cfg:fruitfulfun.common.appleSaplingFromHeroOfTheVillage>
					苹果树苗的来源有点特别。当你成为村庄里的英雄后，小村民会将苹果树苗送给你。
					<#elif cfg:fruitfulfun.common.villageAppleTreeWorldGen>
					苹果树苗的来源有点特别。它只偶尔在平原村庄的中心出现。
					<#else>
					在当前配置下，苹果树苗的默认来源被禁用了。
					<#endif>
					"""));

			page(
					"hybrid",
					() -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withCondition(GuideUtil.moduleLoaded("bee")));
			pageTitle("收集珍奇水果");
			pageText(
					lines("""
							要想解锁更多稀有品种，需要有蜜蜂的帮助。
							
							让蜜蜂携带不同来源的花粉为果树授粉，就可能得到生长有全新水果的树叶。破坏这些特殊的树叶就有概率获得这种水果的树苗。
							
							详见{0}。
							"""), entryLink("果树杂交", "fruit_tree", "hybridizing"));
		}

		@Override
		protected String entryName() {
			return "获取新树种";
		}

		@Override
		protected String entryDescription() {
			return "介绍各种树苗的获得方式。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CherryModule.CHERRY_SAPLING);
		}

		@Override
		protected String entryId() {
			return "getting_new_trees";
		}
	}

	public static class HybridizingEntry extends IndexModeEntryProvider {

		public HybridizingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page(
					"pollen", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("花粉与授粉");
			pageText(lines("""
					蜜蜂会携带自己最近采集过的花粉。当它带着特定组合的花粉为另一种植物授粉时，就有可能产生意想不到的效果，这其中包括杂交。只要花粉组合正确，杂交必然成功，但破坏树叶来获得新树苗就需要一点运气了。
					
					一些更高阶的杂交过程需要拥有「高级授粉」特性的蜜蜂。
					"""));
		}

		@Override
		protected String entryName() {
			return "果树杂交";
		}

		@Override
		protected String entryDescription() {
			return "用花粉培育全新的树种。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(PomegranateModule.POMEGRANATE_SAPLING);
		}

		@Override
		protected String entryId() {
			return "hybridizing";
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("bee"));
		}
	}

	public static class FruitUtilitiesEntry extends IndexModeEntryProvider {

		public FruitUtilitiesEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page(
					"citrus",
					() -> BookSpotlightPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withItem(RecipeUtil.tagIngredient(registries().lookupOrThrow(Registries.ITEM), CoreModule.CITRUS_FRUITS)));
			pageTitle("柑橘");
			pageText(lines("""
					食用柑橘类水果可以扑灭身上的火焰。
					
					柠檬补充的饥饿值较少，但食用速度最快、补充的饱和度也最多。
					"""));

			page(
					"redlove", () -> BookSpotlightPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withItem(CherryModule.REDLOVE));
			pageTitle("红心果");
			pageText(lines("""
					红心果能少量回复生命，并加速生物的繁殖冷却。
					"""));

			page(
					"pomegranate", () -> BookSpotlightPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withItem(PomegranateModule.POMEGRANATE));
			pageTitle("石榴");
			pageText(lines("""
					石榴不可食用。
					
					石榴掉落在地面后如果没有及时被收集，会消失并产生一次伤害极小但推力很强的爆炸。
					"""));
		}

		@Override
		protected String entryName() {
			return "水果的妙用";
		}

		@Override
		protected String entryDescription() {
			return "不仅仅是食用！";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CherryModule.REDLOVE);
		}

		@Override
		protected String entryId() {
			return "fruit_utilities";
		}
	}

	public static class PlantingEntry extends IndexModeEntryProvider {

		public PlantingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page(
					"planting",
					() -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withCondition(GuideUtil.moduleLoaded("bee")));
			pageTitle("寿命");
			pageText(
					lines("""
							果树并非能够一直产出果实。每次尝试结出果实时，都会减少这棵树的寿命。在不经人为照顾的情况下，果树最终会停止结果。
							
							如果破坏了果树的树干或顶端的树叶，果树会立即停止结果。
							<#if bee>
							
							让蜜蜂一直对树叶进行普通授粉（不发生杂交），可以令果树一直结果。
							<#endif>
							"""));

			page(
					"allogamous", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withCondition(GuideUtil.eval("CFG('fruitfulfun.common.allogamousTrees')")));
			pageTitle("自花授粉VS异花授粉");
			pageText(lines("""
					某些果树需要蜜蜂授粉才能结果。你可以在树苗或树叶的气泡提示中查看哪些果树具有这种特性。
					"""));

			page(
					"harvesting", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("快捷收获");
			pageText(lines("""
					除了手动采摘，你还可以用号角收获。
					
					吹响特定的号角，周围果树上的果实会全部落下。
					"""));
		}

		@Override
		protected String entryName() {
			return "种植、生长、收获";
		}

		@Override
		protected String entryDescription() {
			return "专家农场主的必需知识。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CoreModule.APPLE_SAPLING);
		}

		@Override
		protected String entryId() {
			return "planting";
		}
	}
}