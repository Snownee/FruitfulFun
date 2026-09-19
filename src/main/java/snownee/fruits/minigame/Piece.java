package snownee.fruits.minigame;

import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

public record Piece(PieceType type, @Nullable CompoundTag data, @Nullable CompoundTag serverData) {
	public static final String LOOT_TABLE_KEY = "lootTable";
	public static final String BEES_KEY = "bees";
	public static final String LENGTH_KEY = "length";
	public static final String BREAKS_KEY = "breaks";

	public static Piece of(PieceType type) {
		return new Piece(type, null, null);
	}

	/** 深拷贝一份，避免把同一份（可能可变的）NBT 数据放置到多个格子或多次对局中。 */
	public Piece copy() {
		return new Piece(
				type,
				data == null ? null : data.copy(),
				serverData == null ? null : serverData.copy());
	}

	public static Piece large(PieceType type, int length) {
		CompoundTag data = new CompoundTag();
		data.putInt(LENGTH_KEY, length);
		return new Piece(type, data, null);
	}

	public static Piece lootbox() {
		CompoundTag serverData = new CompoundTag();
		serverData.putString(LOOT_TABLE_KEY, MinigameConfig.LOOTBOX_LOOT_TABLE.toString());
		return new Piece(PieceType.LOOTBOX, null, serverData);
	}

	public boolean is(PieceType type) {
		return this.type == type;
	}

	public int iceBreaks() {
		return data == null ? 0 : data.getInt(BREAKS_KEY).orElse(0);
	}

	public Piece withBreaks(int breaks) {
		CompoundTag result = data == null ? new CompoundTag() : data.copy();
		result.putInt(BREAKS_KEY, breaks);
		return new Piece(type, result, serverData);
	}

	public int needLength() {
		return data == null ? 0 : data.getInt(LENGTH_KEY).orElse(0);
	}
}
