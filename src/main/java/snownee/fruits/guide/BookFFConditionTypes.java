package snownee.fruits.guide;

import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;

public final class BookFFConditionTypes {

	public static final BookConditionType<BookFFModuleLoadedCondition> MODULE_LOADED = BookConditionTypeRegistry.register(
			BookFFModuleLoadedCondition.ID,
			BookFFModuleLoadedCondition.CODEC,
			BookFFModuleLoadedCondition.STREAM_CODEC);

	public static void init() {
	}

	private BookFFConditionTypes() {
	}

}