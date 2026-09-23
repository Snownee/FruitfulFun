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
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookViewerRecipePageModel;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("葡萄柚意式奶冻");
			pageText(lines("""
					葡萄柚的酸、奶油的柔、蛋与明胶共同凝成了一轮安静的月亮。传说它诞生于女巫的炼药锅事故：本想做药水，却做出了甜品。入口先苦后甜，像在废弃矿井里找到钻石。村民说，吃下后，雷声都会变轻柔。
					"""));
			page(
					"recipe",
					() -> BookViewerRecipePageModel.create()
							.withRecipeId1("fruitfulfun:grapefruit_panna_cotta")
							.withRecipe1($ -> $
									.withBackground(true)
									.withScale(0.8F)
									.withInput(new ItemStackTemplate(CoreModule.GRAPEFRUIT.asItem()))
									.withOutput(new ItemStackTemplate(FoodModule.GRAPEFRUIT_PANNA_COTTA.asItem()))));
			pageTitle("配方");
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("food"));
		}

		@Override
		protected String entryName() {
			return "葡萄柚意式奶冻";
		}

		@Override
		protected String entryDescription() {
			return "用葡萄柚制成的丝滑奶冻。";
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("多瑙河之波");
			pageText(lines("""
					可可、鸡蛋与奶油一层层叠成波浪，切面便涌出深红与棕黑的河流。红心果像小舟，在巧克力长河里漂流。有人说，吃下这块蛋糕，能听见彼方的圆舞曲；也有人说，那只是饥饿的幻听。
					"""));
			page(
					"recipe",
					() -> BookViewerRecipePageModel.create()
							.withRecipeId1("fruitfulfun:donauwelle")
							.withRecipe1($ -> $
									.withBackground(true)
									.withScale(0.8F)
									.withInput(new ItemStackTemplate(CherryModule.REDLOVE.asItem()))
									.withOutput(new ItemStackTemplate(FoodModule.DONAUWELLE.asItem()))));
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("蜂蜜柚子茶");
			pageText(lines("""
					蜂蜜与柚子熬成金色热饮，蒸腾起袅袅热气。一口下肚，会让人不自觉想起那个你记忆中蜜蜂在阳光中舞蹈的美好午后。要我说，比起端着牛奶桶豪饮，它才是老派探险家的优雅之选。
					"""));
			page(
					"recipe",
					() -> BookViewerRecipePageModel.create()
							.withRecipeId1("fruitfulfun:honey_pomelo_tea")
							.withRecipe1($ -> $
									.withBackground(true)
									.withScale(0.8F)
									.withInput(new ItemStackTemplate(CoreModule.POMELO.asItem()))
									.withOutput(new ItemStackTemplate(FoodModule.HONEY_POMELO_TEA.asItem()))));
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("水果竹筒饭");
			pageText(lines("""
					劈开青竹，蒸汽带着果园的耳语涌出。五彩的果肉碎交织在软糯的米粒间，细嚼之下，既有丛林露水般的清新，又有谷物带来的充沛体力。旅人常带一节上路，吃一口，仿佛脚下不是石砖，而是柔软草地。
					"""));
			page(
					"recipe",
					() -> BookViewerRecipePageModel.create()
							.withRecipeId1("fruitfulfun:rice_with_fruits")
							.withRecipe1($ -> $
									.withBackground(true)
									.withScale(0.8F)
									.withInput(new ItemStackTemplate(CoreModule.TANGERINE.asItem()))
									.withOutput(new ItemStackTemplate(FoodModule.RICE_WITH_FRUITS.asItem()))));
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("柠檬烤鸡");
			pageText(lines("""
					整只鸡抹上柠檬汁与香草，在烤架上慢慢转，直到皮脆得像金锭。咬下去，酸香与肉汁一起爆开，像庆典烟花落在舌尖。厨师坚称，关键不是火候，而是对鸡说「晚安」。
					"""));
			page(
					"recipe",
					() -> BookViewerRecipePageModel.create()
							.withRecipeId1("fruitfulfun:lemon_roast_chicken")
							.withRecipe1($ -> $
									.withBackground(true)
									.withScale(0.8F)
									.withInput(new ItemStackTemplate(CoreModule.LEMON.asItem()))
									.withOutput(new ItemStackTemplate(FoodModule.LEMON_ROAST_CHICKEN_BLOCK.asItem()))));
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
			page("intro", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
			pageTitle("紫颂果派");
			pageText(lines("""
					末地旅人把紫颂果捣成紫色果泥，裹进酥皮，再送入烤箱烘烤。据说那味道并不十分可口，有人曾声称，吃下它后，脑海里久久回荡着末地生灵的那些含混不清的怨语。
					"""));
			page(
					"recipe",
					() -> BookViewerRecipePageModel.create()
							.withRecipeId1("fruitfulfun:chorus_fruit_pie")
							.withRecipe1($ -> $
									.withBackground(true)
									.withScale(0.8F)
									.withInput(new ItemStackTemplate(Items.CHORUS_FRUIT))
									.withOutput(new ItemStackTemplate(FoodModule.CHORUS_FRUIT_PIE.asItem()))));
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