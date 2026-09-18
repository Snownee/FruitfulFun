package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 棋盘上用字符串描述的图案，参考 {@code BlockPatternBuilder} 的写法。
 * <p>
 * 每个字符串是一行；字符 {@code '.'} 或空格表示空，其余字符到棋子的映射由静态表
 * {@link #PIECES} 决定。默认 {@code '#'} 为冰块，可通过 {@link #register(char, Piece)}
 * 注册更多棋子（例如不同颜色/类型的棋子）。图案在调用 {@link #pieces(int)} 时居中
 * 放置到棋盘上。
 * <p>
 * 例如：
 * <pre>
 * BoardPattern.of("###",
 *                 "###",
 *                 "###")
 * </pre>
 */
public final class BoardPattern {
	/** 视为空格的字符。 */
	private static final String EMPTY = ". ";

	/** 图案字符到棋子的全局映射，用于在字符串图案中混用不同棋子。 */
	private static final Map<Character, Piece> PIECES = new LinkedHashMap<>();

	static {
		register('#', Piece.of(PieceType.ICE));
	}

	private final String[] rows;
	private final int width;

	private BoardPattern(String[] rows) {
		if (rows.length == 0) {
			throw new IllegalArgumentException("Empty pattern");
		}
		this.width = rows[0].length();
		for (String row : rows) {
			if (row.length() != width) {
				throw new IllegalArgumentException("Ragged pattern row: '" + row + "'");
			}
		}
		this.rows = rows;
	}

	/** 以字符串行构造图案。 */
	public static BoardPattern of(String... rows) {
		return new BoardPattern(rows.clone());
	}

	/** 注册新的图案字符，用于在字符串图案中放置其他类型的棋子。 */
	public static void register(char key, Piece piece) {
		if (EMPTY.indexOf(key) >= 0) {
			throw new IllegalArgumentException("Reserved pattern character: '" + key + "'");
		}
		PIECES.put(key, piece);
	}

	/**
	 * 将图案居中放置到 {@code boardSize × boardSize} 的棋盘上，返回“格子索引 → 棋子”的有序映射。
	 * 空字符被跳过；未注册的字符或超出棋盘的尺寸直接抛错（尽早失败）。
	 */
	public Map<Integer, Piece> pieces(int boardSize) {
		int height = rows.length;
		if (width > boardSize || height > boardSize) {
			throw new IllegalArgumentException(
					"Pattern " + width + "x" + height + " exceeds board " + boardSize);
		}
		int offsetX = (boardSize - width) / 2;
		int offsetY = (boardSize - height) / 2;
		Map<Integer, Piece> pieces = new LinkedHashMap<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				char key = rows[y].charAt(x);
				if (EMPTY.indexOf(key) >= 0) {
					continue;
				}
				Piece piece = PIECES.get(key);
				if (piece == null) {
					throw new IllegalArgumentException("Unknown pattern character: '" + key + "'");
				}
				pieces.put(offsetX + x + (offsetY + y) * boardSize, piece.copy());
			}
		}
		return pieces;
	}

	/** 图案覆盖的格子索引（按图案书写顺序）。 */
	public List<Integer> cells(int boardSize) {
		return new ArrayList<>(pieces(boardSize).keySet());
	}
}
