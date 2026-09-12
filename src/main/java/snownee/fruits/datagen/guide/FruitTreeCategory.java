package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;

public class FruitTreeCategory extends IndexModeCategoryProvider {

	public FruitTreeCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new GettingNewTreesEntry(this).generate());
		add(new PlantingEntry(this).generate());
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
		return BookIconModel.create(CoreModule.ORANGE_SAPLING.get());
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
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("寻找树苗");
			pageText(lines("""
					模组中的果树不会凭空出现。

					大多数树苗可以从流浪商人处购买，也有一部分就生长在世界各地的果树之下。

					而某些特殊树种，只能靠你亲手培育。
					"""));

			page("apple", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("特别的苹果");
			pageText(lines("""
					苹果树苗不会在世界中自然生成。

					据说……把东西递给村民家的小孩子，会有意想不到的收获。
					"""));

			page("hybrid", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("杂交与授粉");
			pageText(lines("""
					更多的新树种需要借助蜜蜂来完成杂交。

					让蜜蜂携带不同来源的花粉为果树授粉，就可能得到全新的树种。

					具体方法见 {0} 章节。
					"""), categoryLink("养蜂", "beekeeping"));
		}

		@Override
		protected String entryName() {
			return "新树种的获取";
		}

		@Override
		protected String entryDescription() {
			return "树苗的来源与杂交方法。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CherryModule.CHERRY_SAPLING.get());
		}

		@Override
		protected String entryId() {
			return "getting_new_trees";
		}
	}

	public static class PlantingEntry extends IndexModeEntryProvider {

		public PlantingEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("planting", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("种植与生长");
			pageText(lines("""
					像种植原版树木一样种下树苗，等待它慢慢长大。

					果实成熟后，树叶会挂上果子，右击即可采摘。

					光照充足、空间足够时，果树会长得更快。

					如果没有蜜蜂授粉，果树最终会停止结果；有些果树甚至必须靠蜜蜂授粉才能结果，详见 {0} 章节。
					"""), categoryLink("养蜂", "beekeeping"));

			page("harvesting", () -> BookTextPageModel.create()
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
			return "果树种植";
		}

		@Override
		protected String entryDescription() {
			return "种植、生长与收获。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(CoreModule.APPLE_SAPLING.get());
		}

		@Override
		protected String entryId() {
			return "planting";
		}
	}
}