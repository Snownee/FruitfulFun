package snownee.fruits.minigame;

import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

public record Piece(PieceType type, @Nullable CompoundTag data, @Nullable CompoundTag serverData) {
	public static final String LOOT_TABLE_KEY = "lootTable";
	public static final String BEES_KEY = "bees";
	public static final String LENGTH_KEY = "length";

	public static Piece of(PieceType type) {
		return new Piece(type, null, null);
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

	public int needLength() {
		return data == null ? 0 : data.getInt(LENGTH_KEY).orElse(0);
	}
}
