package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSmithingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import snownee.fruits.gadget.GadgetModule;

public class ToolsCategory extends IndexModeCategoryProvider {

	public ToolsCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new BuzzyCrafterEntry(this).generate());
		add(new BuzzyShieldEntry(this).generate());
		add(new ScentedCandlesEntry(this).generate());
		add(new BrewerEntry(this).generate());
		add(new RainDetectorEntry(this).generate());
	}

	@Override
	protected BookCategoryModel additionalSetup(BookCategoryModel category) {
		return super.additionalSetup(category).withCondition(GuideUtil.moduleLoaded("gadget"));
	}

	@Override
	protected String categoryName() {
		return "工具";
	}

	@Override
	protected String categoryDescription() {
		return lines("实用的机器与装备，让农业更高效。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(GadgetModule.BUZZY_CRAFTER.get());
	}

	@Override
	public String categoryId() {
		return "tools";
	}

	public static class BuzzyCrafterEntry extends IndexModeEntryProvider {

		public BuzzyCrafterEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("蜜蜂驱动的合成台");
			pageText(lines("""
					嗡嗡合成台能收集归巢蜜蜂的能量，并利用这份能量自动完成合成。

					归巢的蜜蜂越多、越勤快，它就转得越快。

					右击顶部可以放入材料，空手点击可以取出产物。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:buzzy_crafter"));
		}

		@Override
		protected String entryName() {
			return "嗡嗡合成台";
		}

		@Override
		protected String entryDescription() {
			return "蜜蜂驱动的自动合成台。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.BUZZY_CRAFTER.get());
		}

		@Override
		protected String entryId() {
			return "buzzy_crafter";
		}
	}

	public static class BuzzyShieldEntry extends IndexModeEntryProvider {

		public BuzzyShieldEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("蜜蜂之盾");
			pageText(lines("""
					一面由蜜蜂驱动的盾牌。

					完美格挡（刚举盾的瞬间）会击退周围的敌人，并召唤蜜蜂反击攻击者。

					每次格挡都会消耗能量，注意盾牌上的电量显示。
					"""));
			page("recipe", () -> BookSmithingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:buzzy_shield"));
		}

		@Override
		protected String entryName() {
			return "嗡嗡盾牌";
		}

		@Override
		protected String entryDescription() {
			return "召唤蜜蜂反击的盾牌。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.BUZZY_SHIELD.get());
		}

		@Override
		protected String entryId() {
			return "buzzy_shield";
		}
	}

	public static class ScentedCandlesEntry extends IndexModeEntryProvider {

		public ScentedCandlesEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("气味的力量");
			pageText(lines("""
					点燃不同的香薰蜡烛，会散发出不同的气味，影响周围的生物：
					向日葵味：防止幻翼生成
					樱桃味：防止流浪商人生成
					瓶子草味：阻止传送与末影人搬方块
					郁金香味：削弱生物，并让效果持续时间翻倍
					玫瑰味：防止敌对生物与蝙蝠生成

					它们由蜡烛与对应的植物在锻造台中合成。
					"""));
		}

		@Override
		protected String entryName() {
			return "香薰蜡烛";
		}

		@Override
		protected String entryDescription() {
			return "用气味影响周围的生物。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.PHANTOM_CANDLE.get());
		}

		@Override
		protected String entryId() {
			return "scented_candles";
		}
	}

	public static class BrewerEntry extends IndexModeEntryProvider {

		public BrewerEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("自动酿造");
			pageText(lines("""
					通过龙之仪式，酿造台会转化为酿造机（见{0}章节）。

					酿造机会把产物自动输送到下方的容器。

					持续酿造同一种产物，它还会越酿越快，最高提速 100%。
					"""), categoryLink("仪式", "ritual"));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("ritual"));
		}

		@Override
		protected String entryName() {
			return "酿造机";
		}

		@Override
		protected String entryDescription() {
			return "自动酿造并加速的酿造机。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.BREWER.get());
		}

		@Override
		protected String entryId() {
			return "brewer";
		}
	}

	public static class RainDetectorEntry extends IndexModeEntryProvider {

		public RainDetectorEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("探测降雨");
			pageText(lines("""
					通过龙之仪式获得（见{0}章节）。

					它会像阳光探测器一样输出红石信号，只不过检测的是降雨而非阳光。
					"""), categoryLink("仪式", "ritual"));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("ritual"));
		}

		@Override
		protected String entryName() {
			return "雨探测器";
		}

		@Override
		protected String entryDescription() {
			return "检测降雨的红石方块。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.RAIN_DETECTOR.get());
		}

		@Override
		protected String entryId() {
			return "rain_detector";
		}
	}
}