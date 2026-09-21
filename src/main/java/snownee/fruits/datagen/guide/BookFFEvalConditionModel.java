package snownee.fruits.datagen.guide;

import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;

import net.minecraft.core.HolderLookup;
import snownee.fruits.guide.BookFFEvalCondition;

public class BookFFEvalConditionModel extends BookConditionModel<BookFFEvalConditionModel> {

	private String expression;

	protected BookFFEvalConditionModel() {
		super(BookFFEvalCondition.ID);
	}

	public static BookFFEvalConditionModel create() {
		return new BookFFEvalConditionModel();
	}

	@Override
	public BookCondition toBookCondition(HolderLookup.Provider provider) {
		return new BookFFEvalCondition(tooltipComponent(), expression);
	}

	public String getExpression() {
		return expression;
	}

	public BookFFEvalConditionModel withExpression(String expression) {
		this.expression = expression;
		return this;
	}

}
