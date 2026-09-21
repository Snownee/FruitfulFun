package snownee.fruits.datagen.guide;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;

import snownee.fruits.FruitfulFun;

public final class GuideUtil {

	private static final Pattern DIRECTIVE = Pattern.compile("<#(if|elif|else|endif)(?:\\s.*)?>");

	private GuideUtil() {
	}

	/**
	 * Modonomicon 的 markdown 里，行尾加反斜杠才能强制换行。
	 * <p>
	 * Kiwi 的 TranslationPreprocessor 会改变指令（<#if> 等）旁的换行：
	 * <ul>
	 * <li><#if> 前面的换行会被保留，但整个条件渲染为空时会被删除，因此不能加反斜杠；</li>
	 * <li><#if>/<#elif>/<#else> 后面的换行会被吃掉，需要反斜杠才能保住换行；</li>
	 * <li><#elif>/<#else>/<#endif> 前面的换行会被吃掉，不能加反斜杠；</li>
	 * <li><#endif> 后面的换行会被保留，需要反斜杠。</li>
	 * </ul>
	 * 只有当条件块前面已经渲染出内容时，分支开头才需要硬换行，否则会在页面顶部多出一个空行。
	 */
	public static String lines(String text) {
		if (text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}
		String[] lines = text.split("\n", -1);
		StringBuilder sb = new StringBuilder(text.length() + lines.length);
		// [条件块之前是否已有内容, 各分支中是否产生过内容]
		Deque<boolean[]> conditionals = new ArrayDeque<>();
		boolean hasOutput = false;
		for (int i = 0; i < lines.length; i++) {
			if (i > 0) {
				if (directive(lines[i]) == null) {
					String previous = directive(lines[i - 1]);
					if (previous == null || previous.equals("endif") || !conditionals.isEmpty() && conditionals.peek()[0]) {
						sb.append('\\');
					}
				}
				sb.append('\n');
			}
			String line = lines[i];
			String keyword = directive(line);
			if ("if".equals(keyword)) {
				conditionals.push(new boolean[]{hasOutput, false});
			} else if ("elif".equals(keyword) || "else".equals(keyword)) {
				boolean[] frame = conditionals.peek();
				frame[1] |= hasOutput;
				hasOutput = frame[0];
			} else if ("endif".equals(keyword)) {
				boolean[] frame = conditionals.pop();
				frame[1] |= hasOutput;
				hasOutput = frame[0] || frame[1];
			} else if (!line.isBlank()) {
				hasOutput = true;
			}
			sb.append(line);
		}
		return sb.toString().replace("%", "%%");
	}

	private static String directive(String line) {
		Matcher matcher = DIRECTIVE.matcher(line.trim());
		return matcher.matches() ? matcher.group(1) : null;
	}

	public static BookConditionModel<?> moduleLoaded(String moduleId) {
		return eval("HAS('@" + FruitfulFun.id(moduleId) + "')");
	}

	public static BookConditionModel<?> moduleNotLoaded(String moduleId) {
		return eval("!HAS('@" + FruitfulFun.id(moduleId) + "')");
	}

	public static BookFFEvalConditionModel eval(String expression) {
		return BookFFEvalConditionModel.create().withExpression(expression);
	}

}