package snownee.fruits.guide;

import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;

public final class BookFFConditionTypes {

	public static final BookConditionType<BookFFEvalCondition> EVAL = BookConditionTypeRegistry.register(
			BookFFEvalCondition.ID,
			BookFFEvalCondition.CODEC,
			BookFFEvalCondition.STREAM_CODEC);

	public static void init() {
	}

	private BookFFConditionTypes() {
	}

}