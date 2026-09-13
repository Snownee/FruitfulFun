package snownee.fruits.datagen.guide;

import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;

import snownee.fruits.FruitfulFun;

public final class GuideUtil {

	private GuideUtil() {
	}

	/**
	 * Modonomicon 的 markdown 里，行尾加反斜杠才能强制换行。
	 * 把普通的 \n 转成反斜杠+\n，两个连续的换行就得到一个空行。
	 */
	public static String lines(String text) {
		if (text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}
		return text.replace("\n", "\\\n").replace("%", "%%");
	}

	public static BookConditionModel<?> moduleLoaded(String moduleId) {
		return BookFFModuleLoadedConditionModel.create().withModuleId(FruitfulFun.id(moduleId).toString());
	}

	public static BookConditionModel<?> moduleNotLoaded(String moduleId) {
		return BookFFModuleLoadedConditionModel.create().withModuleId(FruitfulFun.id(moduleId).toString()).withInverted(true);
	}

}