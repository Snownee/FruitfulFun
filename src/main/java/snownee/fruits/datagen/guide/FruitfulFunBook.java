package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.book.BookDisplayMode;

import snownee.fruits.FruitfulFun;

public class FruitfulFunBook extends SingleBookSubProvider {

	public FruitfulFunBook() {
		super("guide", FruitfulFun.ID);
	}

	@Override
	protected BookModel additionalSetup(BookModel book) {
		return book.withDisplayMode(BookDisplayMode.INDEX)
				.withGenerateBookItem(false)
				.withCustomBookItem(modLoc("handbook"))
				.withShowRecentlyUnlocked(false);
	}

	@Override
	protected void registerDefaultMacros() {
	}

	@Override
	protected void generateCategories() {
		add(new FruitTreeCategory(this).generate());
		add(new BeekeepingCategory(this).generate());
		add(new RitualCategory(this).generate());
		add(new ToolsCategory(this).generate());
		add(new CookingCategory(this).generate());
	}

	@Override
	protected String bookName() {
		return "果实乐趣手册";
	}

	@Override
	protected String bookTooltip() {
		return "一本记录水果、蜜蜂与仪式的手册。";
	}

	@Override
	protected String bookDescription() {
		return lines("""
				欢迎阅读《果实乐趣手册》！

				这本书记录了 Fruitful Fun 模组的种种玩法：
				果树、养蜂、仪式、工具与烹饪。

				有些内容需要你亲自去发现。
				""");
	}
}