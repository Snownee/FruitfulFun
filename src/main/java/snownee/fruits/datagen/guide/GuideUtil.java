package snownee.fruits.datagen.guide;

public final class GuideUtil {

	private GuideUtil() {
	}

	/**
	 * Modonomicon 的 markdown 里，行尾加反斜杠才能强制换行。
	 * 把普通的 \n 转成反斜杠+\n，两个连续的换行就得到一个空行。
	 */
	public static String lines(String text) {
		return text.replace("\n", "\\\n");
	}
}