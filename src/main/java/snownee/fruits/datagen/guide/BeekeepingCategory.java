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
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import net.minecraft.world.item.Items;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.cherry.CherryModule;

public class BeekeepingCategory extends IndexModeCategoryProvider {

	public BeekeepingCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new BeeIntroEntry(this).generate());
		add(new InspectorEntry(this).generate());
		add(new TraitsEntry(this).generate());
		add(new HybridizingEntry(this).generate());
		add(new RidingEntry(this).generate());
		add(new HauntingEntry(this).generate());
		add(new MerchantsEntry(this).generate());
		add(new MutagenEntry(this).generate());
	}

	@Override
	protected BookCategoryModel additionalSetup(BookCategoryModel category) {
		return super.additionalSetup(category).withCondition(GuideUtil.moduleLoaded("bee"));
	}

	@Override
	protected String categoryName() {
		return "养蜂";
	}

	@Override
	protected String categoryDescription() {
		return lines("蜜蜂不仅酿蜜，还能帮你培育新树种。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(BeeModule.INSPECTOR.get());
	}

	@Override
	public String categoryId() {
		return "beekeeping";
	}

	public static class BeeIntroEntry extends IndexModeEntryProvider {

		public BeeIntroEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("蜜蜂的新面貌");
			pageText(lines("""
					本模组为蜜蜂添加了全新的玩法。

					蜜蜂可以采集花粉、记住花粉的来源，并为你授粉出新的果树。

					用蜜脾就能把蜜蜂从蜂巢里带出来，方便转移蜂群。

					蜜蜂会慢慢自行恢复生命，新出生的蜜蜂还会随机获得一个名字。
					"""));
		}

		@Override
		protected String entryName() {
			return "认识蜜蜂";
		}

		@Override
		protected String entryDescription() {
			return "蜜蜂的全新玩法。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(Items.BEE_SPAWN_EGG);
		}

		@Override
		protected String entryId() {
			return "bee_intro";
		}
	}

	public static class InspectorEntry extends IndexModeEntryProvider {

		public InspectorEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("研究蜜蜂");
			pageText(lines("""
					嗡嗡分析仪是研究蜜蜂的必备工具。

					对蜜蜂使用，可以看到它的特性与携带的花粉。

					副手拿一本书与笔，再对蜜蜂使用，可以把记录写进书里。

					对书架使用，可以为基因命名；配合香薰蜡烛，对方块使用还能查看残留的气味。
					"""));
			page("recipe", () -> BookCraftingRecipePageModel.create()
					.withRecipeId1("fruitfulfun:inspector"));
		}

		@Override
		protected String entryName() {
			return "嗡嗡分析仪";
		}

		@Override
		protected String entryDescription() {
			return "研究蜜蜂特性与基因的必备工具。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(BeeModule.INSPECTOR.get());
		}

		@Override
		protected String entryId() {
			return "inspector";
		}
	}

	public static class TraitsEntry extends IndexModeEntryProvider {

		public TraitsEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("特性与基因");
			pageText(lines("""
					蜜蜂的特性决定了它们的能力与性格。

					有些蜜蜂跑得更快，有些性情温和，有些甚至能当坐骑。

					每个世界里蜜蜂的基因代码都是随机生成的，效果需要你亲自摸索。

					安装 Jade 模组可以获得更直观的基因与花粉信息。
					"""));
		}

		@Override
		protected String entryName() {
			return "蜜蜂特性";
		}

		@Override
		protected String entryDescription() {
			return "能力与性格的差异。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(Items.SUGAR);
		}

		@Override
		protected String entryId() {
			return "traits";
		}
	}

	public static class HybridizingEntry extends IndexModeEntryProvider {

		public HybridizingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("pollen", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("花粉与授粉");
			pageText(lines("""
					蜜蜂会记住自己采集过的花粉。

					当它带着特定组合的花粉为另一棵树授粉时，就有可能结出全新的树种。

					拥有「高级授粉」特性的蜜蜂，能解锁更稀有的杂交配方。
					"""));
			page("examples", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("一点提示");
			pageText(lines("""
					同一个树族之间相互杂交，往往就能诞生新的成员。

					试试让蜜蜂在不同品种的树叶之间往返，再观察果树的反应。
					"""));
		}

		@Override
		protected String entryName() {
			return "授粉与杂交";
		}

		@Override
		protected String entryDescription() {
			return "用花粉培育全新的树种。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CherryModule.CHERRY.get());
		}

		@Override
		protected String entryId() {
			return "hybridizing";
		}
	}

	public static class RidingEntry extends IndexModeEntryProvider {

		public RidingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("骑蜂飞行");
			pageText(lines("""
					只有你自己繁殖出来的、拥有「可骑乘」特性的蜜蜂才允许装鞍，幼蜂不行。

					飞行高度有限制：雨天无法飞行，某些群系也限制了高度。

					用剪刀可以卸下鞍。
					"""));
		}

		@Override
		protected String entryName() {
			return "骑乘蜜蜂";
		}

		@Override
		protected String entryDescription() {
			return "骑着蜜蜂翱翔天际。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(Items.SADDLE);
		}

		@Override
		protected String entryId() {
			return "riding";
		}
	}

	public static class HauntingEntry extends IndexModeEntryProvider {

		public HauntingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("鬼魂蜂的秘密");
			pageText(lines("""
					鬼魂蜂能让你的灵魂「附身」到其他生物身上，从它们的视角行动。

					粉红蜂还藏着属于自己的特殊技能。

					至于如何得到鬼魂蜂——见{0}章节。
					"""), categoryLink("仪式", "ritual"));
		}

		@Override
		protected BookEntryModel additionalSetup(BookEntryModel entry) {
			return super.additionalSetup(entry).withCondition(GuideUtil.moduleLoaded("ritual"));
		}

		@Override
		protected String entryName() {
			return "闹鬼与鬼魂蜂";
		}

		@Override
		protected String entryDescription() {
			return "附身与操控的奇妙能力。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(Items.BELL);
		}

		@Override
		protected String entryId() {
			return "haunting";
		}
	}

	public static class MerchantsEntry extends IndexModeEntryProvider {

		public MerchantsEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("从商人手中获得蜜蜂");
			pageText(lines("""
					流浪商人偶尔会出售珍贵的树苗。

					养蜂人村民会出售蜂箱。

					若世界中找不到养蜂人，流浪商人会代为出售蜂箱。
					"""));
		}

		@Override
		protected String entryName() {
			return "商人与蜜蜂";
		}

		@Override
		protected String entryDescription() {
			return "从商人手中获得树苗与蜂箱。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(Items.EMERALD);
		}

		@Override
		protected String entryId() {
			return "merchants";
		}
	}

	public static class MutagenEntry extends IndexModeEntryProvider {

		public MutagenEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("酿造突变剂");
			pageText(lines("""
					在酿造台上酿造突变剂，然后喂给蜜蜂。

					受到影响的蜜蜂，产下的后代更容易发生变异。

					酿坏的突变剂没法使用，但可以回收成玻璃瓶。
					"""));
		}

		@Override
		protected String entryName() {
			return "突变剂";
		}

		@Override
		protected String entryDescription() {
			return "让蜜蜂的后代更容易变异。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(BeeModule.MUTAGEN.get());
		}

		@Override
		protected String entryId() {
			return "mutagen";
		}
	}
}