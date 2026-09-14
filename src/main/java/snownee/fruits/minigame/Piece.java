package snownee.fruits.minigame;

import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

public record Piece(PieceType type, @Nullable CompoundTag data, @Nullable CompoundTag serverData) {
	public static final String LOOT_TABLE_KEY = "lootTable";

	public static Piece of(PieceType type) {
		return new Piece(type, null, null);
	}

	public static Piece lootbox() {
		CompoundTag serverData = new CompoundTag();
		serverData.putString(LOOT_TABLE_KEY, MinigameConfig.LOOTBOX_LOOT_TABLE.toString());
		return new Piece(PieceType.LOOTBOX, null, serverData);
	}

	public boolean is(PieceType type) {
		return this.type == type;
	}
}
