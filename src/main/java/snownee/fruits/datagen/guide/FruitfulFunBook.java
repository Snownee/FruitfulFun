package snownee.fruits.datagen.guide;

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
				.withCustomBookItem(modLoc("guide"))
				.withShowRecentlyUnlocked(false)
				.withTheme(theme -> theme.withId(modLoc("theme")));
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
		return "book.fruitfulfun.guide.name";
	}

	@Override
	protected String bookTooltip() {
		return "book.fruitfulfun.guide.tooltip";
	}
}