package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import snownee.fruits.cherry.CherryModule;
import snownee.fruits.food.FoodModule;

public class CookingCategory extends IndexModeCategoryProvider {

	public CookingCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new FruitEatingEntry(this).generate());
		add(new DishesEntry(this).generate());
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

	public static class FruitEatingEntry extends IndexModeEntryProvider {

		public FruitEatingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("水果的妙用");
			pageText(lines("""
					模组的果实都能直接食用。

					柠檬的食用速度最快。

					食用柑橘类水果可以扑灭身上的火焰。

					红心果能少量回复生命，并加速生物的繁殖冷却。
					"""));
		}

		@Override
		protected String entryName() {
			return "水果的食用";
		}

		@Override
		protected String entryDescription() {
			return "直接食用水果的种种妙用。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CherryModule.REDLOVE.get());
		}

		@Override
		protected String entryId() {
			return "fruit_eating";
		}
	}

	public static class DishesEntry extends IndexModeEntryProvider {

		public DishesEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("丰收的料理");
			pageText(lines("""
					用收获的果实制作各种甜点与正餐。

					若安装了农夫乐事（Farmer's Delight），西柚意式奶冻、蜂蜜柚子茶与水果竹筒饭需要使用烹饪锅制作。
					"""));
			page("grapefruit_panna_cotta", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("西柚意式奶冻");
			pageText(lines("""
					用西柚、奶油、鸡蛋、明胶与糖制成的丝滑奶冻。
					"""));
			page("donauwelle", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:donauwelle")
					.withTitle1(context().pageTitle()));
			pageTitle("多瑙河之波");
			page("honey_pomelo_tea", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("蜂蜜柚子茶");
			pageText(lines("""
					用柚子与蜂蜜泡成的热饮，饮用后可以清除有害效果。
					"""));
			page("rice_with_fruits", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("水果竹筒饭");
			pageText(lines("""
					用竹筒蒸熟的米饭，混入各色水果，清香可口。
					"""));
			page("lemon_roast_chicken", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:lemon_roast_chicken")
					.withTitle1(context().pageTitle()));
			pageTitle("柠檬烤鸡");
			page("chorus_fruit_pie", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:chorus_fruit_pie")
					.withTitle1(context().pageTitle()));
			pageTitle("紫颂果派");
		}

		@Override
		protected String entryName() {
			return "甜点与正餐";
		}

		@Override
		protected String entryDescription() {
			return "把果实变成美味料理。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.GRAPEFRUIT_PANNA_COTTA.get());
		}

		@Override
		protected String entryId() {
			return "dishes";
		}
	}
}