package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import net.minecraft.world.item.Items;
import snownee.fruits.food.FoodModule;

public class RitualCategory extends IndexModeCategoryProvider {

	public RitualCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new DragonRitualEntry(this).generate());
	}

	@Override
	protected BookCategoryModel additionalSetup(BookCategoryModel category) {
		return super.additionalSetup(category)
				.withCondition(GuideUtil.moduleLoaded("ritual"))
				.withEntryToOpen(modLoc("ritual/dragon_ritual"), false);
	}

	@Override
	protected String categoryName() {
		return "仪式";
	}

	@Override
	protected String categoryDescription() {
		return lines("用龙首与烛光举行古老的仪式。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(Items.DRAGON_HEAD);
	}

	@Override
	public String categoryId() {
		return "ritual";
	}

	public static class DragonRitualEntry extends IndexModeEntryProvider {

		public DragonRitualEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page(
					"intro", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("搭建阵型");
			pageText(lines("""
					要搭建仪式场所，你需要至少1个龙首、1块紫颂果派和12根任意的蜡烛。摆放方式如图所示。
					
					你需要最后放置紫颂果派来开始仪式。仪式开始后，应当尽快将想要转化的物品投入中央。
					
					如果没有及时投入物品，将会在地面产生额外的龙息。龙首越多（至多4个），产出的龙息越多。请小心，不要被龙息伤到。
					"""));
			page(
					"multiblock", () -> BookMultiblockPageModel.create()
							.withMultiblockId(modLoc("ritual"))
							.withText(context().pageText()));
			pageText(lines("图中展示了完整的仪式结构。"));

			page(
					"recipes", () -> BookTextPageModel.create()
							.withTitle(context().pageTitle())
							.withText(context().pageText()));
			pageTitle("仪式配方");
			pageText(
					lines("""
							仪式可以把物品转化为另一种物品。以下是一些已为人知的配方：
							
							玻璃瓶 → 龙息
							石榴 → 附魔石榴
							<#if gadget>
							酿造台 → {0}
							阳光探测器 → {1}
							<#endif>
							"""),
					entryLink("酿造机", "tools", "brewer"),
					entryLink("雨天探测器", "tools", "rain_detector"));
		}

		@Override
		protected String entryName() {
			return "龙之仪式";
		}

		@Override
		protected String entryDescription() {
			return "搭建阵型，转化物品。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.CHORUS_FRUIT_PIE);
		}

		@Override
		protected String entryId() {
			return "dragon_ritual";
		}
	}
}