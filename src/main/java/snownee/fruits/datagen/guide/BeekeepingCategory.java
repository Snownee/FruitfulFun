package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import net.minecraft.world.item.Items;
import snownee.fruits.bee.BeeModule;

public class BeekeepingCategory extends IndexModeCategoryProvider {

	public BeekeepingCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new BeeIntroEntry(this).generate());
		add(new GeneticsEntry(this).generate());
		add(new RidingEntry(this).generate());
		add(new HauntingEntry(this).generate());
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
		return lines("那些鲜为人知的蜜蜂知识。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(BeeModule.INSPECTOR);
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
			page(
					"traits", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("基因与性状");
			pageText(lines("""
					蜜蜂的特性决定了它们的能力与性格。
					
					有些蜜蜂速度更快，有些性情温和，有些善于战斗，有些甚至能当坐骑。这些特性都是由它们的基因决定的，并且可以遗传给后代。
					"""));

			page(
					"transporting", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("轻松转运蜜蜂");
			pageText(lines("""
					对蜂巢或蜂箱使用蜜脾，然后在之后一段时间内，你就可以如拿着精准采集工具般挖掘这个方块，而不惹怒其中的蜜蜂。
					
					使用拴绳拴住蜜蜂后对着蜂巢或蜂箱按下使用键后，可以令蜜蜂立即归巢。
					
					吹响特定的号角，可以令周围的蜜蜂立即归巢。
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

	public static class GeneticsEntry extends IndexModeEntryProvider {

		public GeneticsEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page(
					"intro", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("研究蜜蜂");
			pageText(lines("""
					嗡嗡分析仪是研究蜜蜂的必备工具。对蜜蜂长按交互键，可以看到它的特性、基因型与携带的花粉。<#if cfg:fruitfulfun.common.inspectorShowOffspringPotential>潜行状态下交互可以锁定一只蜜蜂，然后当你分析其他蜜蜂时，就会显示它与被锁定蜜蜂的后代可能出现的特性变化。<#endif>
					
					副手拿一本书与笔，再对蜜蜂使用，可以把记录写进书里。对书架使用，可以为基因代号取个新名字。
					"""));

			page(
					"mutagen", () -> BookSpotlightPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withItem(BeeModule.MUTAGEN));
			pageTitle("酿造突变剂");
			pageText(lines("""
					突变剂可以大幅提高特定基因在后代中的突变率。使用方法：繁殖前手持突变剂与亲代交互。
					<#if cfg:fruitfulfun.common.mutagenRecipe>
					
					突变剂由瓶子草酿造而成<#if cfg:fruitfulfun.common.imperfectMutagenChance!=0>，并且有一定概率酿制失败。酿坏的突变剂没法使用，但可以回收成玻璃瓶<#endif>。
					<#endif>
					"""));

			page(
					"trade", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText())
							.withCondition(GuideUtil.eval("CFG('fruitfulfun.common.beehiveTrade')")));
			pageTitle("蜜蜂交易");
			pageText(lines("""
					<#if FFBeekeeper>蜂农<#else>流浪商人<#endif>有时会向你收购带有蜜蜂的蜂箱。蜜蜂的特性越稀有，他们的出价就越高。
					"""));
		}

		@Override
		protected String entryName() {
			return "深入基因学";
		}

		@Override
		protected String entryDescription() {
			return "研究蜜蜂特性与基因的必备工具。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(BeeModule.MUTAGEN);
		}

		@Override
		protected String entryId() {
			return "genetics";
		}
	}

	public static class RidingEntry extends IndexModeEntryProvider {

		public RidingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page(
					"intro", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("骑蜂飞行");
			pageText(lines("""
					你可以给拥有「可骑乘」特性的蜜蜂装上鞍，然后骑上它<#if cfg:fruitfulfun.common.beeRiding.heightLimit>低空<#endif>飞行。一般来说，只有由你繁殖的成年蜜蜂才会如此信任你，允许你骑乘它。
					<#if cfg:fruitfulfun.common.beeRiding.rainingLimit || cfg:fruitfulfun.common.beeRiding.beeRidingEnvironmentAttrRules>
					
					骑乘蜜蜂也存在一些环境限制。<#if cfg:fruitfulfun.common.beeRiding.rainingLimit>比如，没有「耐雨性」的蜜蜂无法在雨中被骑乘。<#endif>
					<#endif>
					
					对蜜蜂使用剪刀可以卸下鞍。
					"""));
		}

		@Override
		protected String entryName() {
			return "骑乘蜜蜂";
		}

		@Override
		protected String entryDescription() {
			return "小众的交通方式，兼顾速度和风格。";
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
			page(
					"intro", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("鬼魂蜂的秘密");
			pageText(
					lines("""
							鬼魂蜂能让你的灵魂「附身」到其他生物上，并且时刻以被附身者的视角旁观他们的行动。
							
							要实现「附身」，要先与鬼魂蜂交互，附身到鬼魂蜂上，然后操控它尽快靠近你想要附身的目标。这时再与目标交互就能附身到目标了。附身时按下潜行键可以立即解除附身。
							"""));

			page(
					"exorcise", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("驱赶附身者");
			pageText(
					lines("""
							如果想要驱赶走附身在自己身上的捣蛋鬼，你需要站在火中承受一段时间的伤害（经测试，灵魂火焰的效果立竿见影）。附身者解除附身的时刻最为脆弱，受伤时会承受更多伤害。
							"""));
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
}