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
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.market.MarketModule;

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
		add(new MarketEntry(this).generate());
	}

	@Override
	protected BookCategoryModel additionalSetup(BookCategoryModel category) {
		return super.additionalSetup(category).withCondition(GuideUtil.eval("HAS('@fruitfulfun:gadget') || HAS('@fruitfulfun:market')"));
	}

	@Override
	protected String categoryName() {
		return "工具";
	}

	@Override
	protected String categoryDescription() {
		return lines("实用的机器与装备，让生活更便利。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(GadgetModule.BUZZY_CRAFTER);
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
			page("intro", () -> BookSpotlightPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()))
					.withItem(GadgetModule.BUZZY_CRAFTER);
			pageTitle("嗡嗡合成台");
			pageText(lines("""
					嗡嗡合成台可以收集归巢蜜蜂的能量，并将这份能量注入其上方的物品或方块。
					
					自动化这一过程十分轻松：将物品丢在合成台上方，物品会被自动吸入容器。在合成台上方放置一个完整方块，容器中的物品会从合成台下方弹出。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create().withRecipeId1("fruitfulfun:buzzy_crafter"));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("gadget"));
		}

		@Override
		protected String entryName() {
			return "嗡嗡合成台";
		}

		@Override
		protected String entryDescription() {
			return "蜜蜂驱动的合成装置。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.BUZZY_CRAFTER);
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
			page("intro", () -> BookSpotlightPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText())
					.withItem(GadgetModule.BUZZY_SHIELD));
			pageTitle("蜂群之盾");
			pageText(
					lines("""
							一面由蜜脾打造而成的盾牌。
							
							举盾时只能抵消半数伤害。但完美格挡（在受击前的一刻举盾）会抵消全部伤害，击退周围的敌人，并召唤蜂群反击攻击者。
							
							每次格挡都会消耗{0}，注意盾牌上的能量显示。
							"""), entryLink("能量", "tools", "buzzy_crafter"));
			page("recipe", () -> BookSmithingRecipePageModel.create().withRecipeId1("fruitfulfun:buzzy_shield"));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("gadget"));
		}

		@Override
		protected String entryName() {
			return "嗡嗡盾牌";
		}

		@Override
		protected String entryDescription() {
			return "能召唤蜂群的盾牌。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.BUZZY_SHIELD);
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("植物的魔法");
			pageText(
					lines("""
							点燃香味蜡烛，其散发的魔力能够影响周围的区块。根据堆叠蜡烛数量的不同，其影响的范围可从1个区块增至最高7×7个区块。
							
							香味蜡烛需要消耗能量来保持燃烧状态。你可以将蜡烛放置在{0}上，一边充能，一边让其生效。
							<#if bee>
							
							对地面使用嗡嗡分析仪可以查看当前区块内生效的气味。
							<#endif>
							"""),
					entryLink("嗡嗡合成台", "tools", "buzzy_crafter"));

			String[] candles = {
					"phantom_candle", "wandering_trader_candle", "ender_candle", "weak_candle", "peace_candle"};
			for (String candle : candles) {
				page(
						candle,
						() -> BookSmithingRecipePageModel.create()
								.withRecipeId1("fruitfulfun:" + candle)
								.withText("block.fruitfulfun." + candle + ".tip.shift"));
			}
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("gadget"));
		}

		@Override
		protected String entryName() {
			return "香味蜡烛";
		}

		@Override
		protected String entryDescription() {
			return "用气味影响周围的生物。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.PHANTOM_CANDLE);
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
			page(
					"intro",
					() -> BookSpotlightPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withItem(GadgetModule.BREWER));
			pageTitle("酿造机");
			pageText(lines("""
					酿造机能把产物自动输送到下方的容器。
					
					持续酿造同一种产物，它还会越酿越快，最高提速100%。
					"""));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry)
					.withCondition(GuideUtil.moduleLoaded("gadget"))
					.withCondition(GuideUtil.moduleLoaded("ritual"));
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
			return BookIconModel.create(GadgetModule.BREWER);
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
			page(
					"intro",
					() -> BookSpotlightPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withItem(GadgetModule.RAIN_DETECTOR));
			pageTitle("雨天探测器");
			pageText(lines("""
					它的工作方式和阳光探测器差不多，只不过它根据降水强度输出红石信号。
					"""));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry)
					.withCondition(GuideUtil.moduleLoaded("gadget"))
					.withCondition(GuideUtil.moduleLoaded("ritual"));
		}

		@Override
		protected String entryName() {
			return "雨天探测器";
		}

		@Override
		protected String entryDescription() {
			return "检测降雨的红石方块。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(GadgetModule.RAIN_DETECTOR);
		}

		@Override
		protected String entryId() {
			return "rain_detector";
		}
	}

	public static class MarketEntry extends IndexModeEntryProvider {

		public MarketEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookSpotlightPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText())
					.withItem(MarketModule.MARKET));
			pageTitle("自动商店");
			pageText(lines("""
					市场方块是一个会自动进货农产品的商店。你只需要存入金钱、下达订单，它每天日出时都会把订好的货物补齐，等待你取走。
					
					将货币放入界面右侧的槽位即可存入金钱。你可以将鼠标悬停在该槽位上来查看支持的货币与对应的面额。
					"""));

			page("ordering", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("下单与进货");
			pageText(lines("""
					点击「订货」按钮后，左键点击任意格子可以打开商品目录，选择物品与数量即可下单；右键点击格子则取消订单。订单会以半透明物品显示。
					
					商品目录只会列出你见过的物品：曾经持有、合成、使用、丢弃过，并且有标价的物品。
					
					每个游戏日的日出时，市场都会用存有的金钱按订单进货，直到补满或金钱耗尽为止。
					"""));

			page("recipe", () -> BookCraftingRecipePageModel.create().withRecipeId1("fruitfulfun:market"));

			page("shortcuts", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("快捷操作");
			pageText(lines("""
					订货模式开启时，按住Ctrl拖拽某个订单格子，可以把它复制到另一个格子。
					
					按下Ctrl+C可以将当前全部订单复制到剪贴板，Ctrl+V则会把剪贴板中的订单粘贴到界面并覆盖现有订单。
					"""));

			page(
					"dynamic_pricing", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withCondition(GuideUtil.eval("CFG('fruitfulfun.common.market.dynamicPricing.mode') != 'Disabled'")));
			pageTitle("无形的大手……（模拟）");
			pageText(lines("""
					<#if CFG('fruitfulfun.common.market.dynamicPricing.mode') == 'Incremental'>
					每天，市场会根据整个世界的需求量动态调整商品价格。商品需求变高后，价格会上升，需求变低后，价格也会相应回落。
					<#else>
					每天，市场会根据整个世界的需求量动态调整商品价格。商品需求变高后，价格会上升，需求稳定后，价格会逐渐回落。
					<#endif>
					"""));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("market"));
		}

		@Override
		protected String entryName() {
			return "市场方块";
		}

		@Override
		protected String entryDescription() {
			return "按订单自动进货的商店。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(MarketModule.MARKET);
		}

		@Override
		protected String entryId() {
			return "market";
		}
	}
}