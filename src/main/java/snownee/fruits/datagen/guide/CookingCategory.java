package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import snownee.fruits.food.FoodModule;

public class CookingCategory extends IndexModeCategoryProvider {

	public CookingCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new GrapefruitPannaCottaEntry(this).generate());
		add(new DonauwelleEntry(this).generate());
		add(new HoneyPomeloTeaEntry(this).generate());
		add(new RiceWithFruitsEntry(this).generate());
		add(new LemonRoastChickenEntry(this).generate());
		add(new ChorusFruitPieEntry(this).generate());
	}

	@Override
	protected BookCategoryModel additionalSetup(BookCategoryModel category) {
		return super.additionalSetup(category).withCondition(GuideUtil.moduleLoaded("food"));
	}

	@Override
	protected String categoryName() {
		return "烹饪";
	}

	@Override
	protected String categoryDescription() {
		return lines("把丰收的果实变成美味佳肴。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(FoodModule.GRAPEFRUIT_PANNA_COTTA.get());
	}

	@Override
	public String categoryId() {
		return "cooking";
	}

	public static class GrapefruitPannaCottaEntry extends IndexModeEntryProvider {

		public GrapefruitPannaCottaEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("西柚意式奶冻");
			pageText(lines("""
					用西柚、奶油、鸡蛋、明胶与糖制成的丝滑奶冻。

					若安装了农夫乐事（Farmer's Delight），需要使用烹饪锅制作。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:grapefruit_panna_cotta")
					.withTitle1(context().pageTitle())
					.withCondition(GuideUtil.moduleNotLoaded("farmersdelight")));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("food"));
		}

		@Override
		protected String entryName() {
			return "西柚意式奶冻";
		}

		@Override
		protected String entryDescription() {
			return "用西柚制成的丝滑奶冻。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.GRAPEFRUIT_PANNA_COTTA.get());
		}

		@Override
		protected String entryId() {
			return "grapefruit_panna_cotta";
		}
	}

	public static class DonauwelleEntry extends IndexModeEntryProvider {

		public DonauwelleEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("多瑙河之波");
			pageText(lines("""
					红心果与巧克力交织的经典蛋糕。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:donauwelle")
					.withTitle1(context().pageTitle()));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(BookAndConditionModel.create()
					.withChildren(GuideUtil.moduleLoaded("food"), GuideUtil.moduleLoaded("cherry")));
		}

		@Override
		protected String entryName() {
			return "多瑙河之波";
		}

		@Override
		protected String entryDescription() {
			return "红心果与巧克力交织的经典蛋糕。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.DONAUWELLE.get());
		}

		@Override
		protected String entryId() {
			return "donauwelle";
		}
	}

	public static class HoneyPomeloTeaEntry extends IndexModeEntryProvider {

		public HoneyPomeloTeaEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("蜂蜜柚子茶");
			pageText(lines("""
					用柚子与蜂蜜泡成的热饮，饮用后可以清除有害效果。

					若安装了农夫乐事（Farmer's Delight），需要使用烹饪锅制作。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:honey_pomelo_tea")
					.withTitle1(context().pageTitle())
					.withCondition(GuideUtil.moduleNotLoaded("farmersdelight")));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("food"));
		}

		@Override
		protected String entryName() {
			return "蜂蜜柚子茶";
		}

		@Override
		protected String entryDescription() {
			return "清除有害效果的热饮。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.HONEY_POMELO_TEA.get());
		}

		@Override
		protected String entryId() {
			return "honey_pomelo_tea";
		}
	}

	public static class RiceWithFruitsEntry extends IndexModeEntryProvider {

		public RiceWithFruitsEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("水果竹筒饭");
			pageText(lines("""
					用竹筒蒸熟的米饭，混入各色水果，清香可口。

					若安装了农夫乐事（Farmer's Delight），需要使用烹饪锅制作。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:rice_with_fruits")
					.withTitle1(context().pageTitle())
					.withCondition(GuideUtil.moduleNotLoaded("farmersdelight")));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("food"));
		}

		@Override
		protected String entryName() {
			return "水果竹筒饭";
		}

		@Override
		protected String entryDescription() {
			return "清香可口的水果竹筒饭。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.RICE_WITH_FRUITS.get());
		}

		@Override
		protected String entryId() {
			return "rice_with_fruits";
		}
	}

	public static class LemonRoastChickenEntry extends IndexModeEntryProvider {

		public LemonRoastChickenEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("柠檬烤鸡");
			pageText(lines("""
					用柠檬与香草慢烤的整鸡，香气扑鼻。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:lemon_roast_chicken")
					.withTitle1(context().pageTitle()));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("food"));
		}

		@Override
		protected String entryName() {
			return "柠檬烤鸡";
		}

		@Override
		protected String entryDescription() {
			return "香气扑鼻的柠檬烤鸡。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.LEMON_ROAST_CHICKEN.get());
		}

		@Override
		protected String entryId() {
			return "lemon_roast_chicken";
		}
	}

	public static class ChorusFruitPieEntry extends IndexModeEntryProvider {

		public ChorusFruitPieEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("紫颂果派");
			pageText(lines("""
					用紫颂果烘烤而成的派，蕴含神秘的力量。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:chorus_fruit_pie")
					.withTitle1(context().pageTitle()));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("food"));
		}

		@Override
		protected String entryName() {
			return "紫颂果派";
		}

		@Override
		protected String entryDescription() {
			return "蕴含神秘力量的紫颂果派。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.CHORUS_FRUIT_PIE.get());
		}

		@Override
		protected String entryId() {
			return "chorus_fruit_pie";
		}
	}
}