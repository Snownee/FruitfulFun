package snownee.fruits.minigame;

import java.util.Optional;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.lychee.util.codec.LycheeCodecs;

public enum PieceType {
	ORANGE("orange", CoreModule.ORANGE),
	LEMON("lemon", CoreModule.LEMON),
	CHERRY("cherry", CherryModule.CHERRY),
	CHORUS("chorus", () -> Items.CHORUS_FRUIT),
	LOOTBOX("lootbox", () -> Items.BUNDLE, true, false, true),
	// 非基础但普通的piece，某些规则中连续消除后会生成
	REDLOVE("redlove", CherryModule.REDLOVE, false, false, true),
	POMEGRANATE("pomegranate", PomegranateModule.POMEGRANATE_ITEM, true, false, true),
	GOLDEN_APPLE("golden_apple", () -> Items.GOLDEN_APPLE, false, true, true),
	BEE("bee", () -> Items.BEE_SPAWN_EGG, false, true, true),
	BEEHIVE("beehive", () -> Items.BEE_NEST, false, false, true, true);

	public static final WeightedList<PieceType> FRUITS = WeightedList.<PieceType>builder()
			.add(ORANGE, 20)
			.add(LEMON, 20)
			.add(CHERRY, 20)
			.add(CHORUS, 20)
			.add(GOLDEN_APPLE, 10)
			.build();
	public static final StreamCodec<ByteBuf, PieceType> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(PieceType::byCode, PieceType::code);
	private static final PieceType[] VALUES = values();

	private final String name;
	private final Supplier<? extends Item> item;
	private final boolean unlinkable;
	private final boolean wildcard;
	private final boolean passThrough;
	private final Component displayName;
	private final @Nullable Component tooltip;
	private volatile @Nullable ItemStackTemplate template;

	PieceType(String name, Supplier<? extends Item> item) {
		this(name, item, false, false, false, false);
	}

	PieceType(String name, Supplier<? extends Item> item, boolean unlinkable) {
		this(name, item, unlinkable, false, false, false);
	}

	PieceType(String name, Supplier<? extends Item> item, boolean unlinkable, boolean wildcard) {
		this(name, item, unlinkable, wildcard, false, false);
	}

	PieceType(String name, Supplier<? extends Item> item, boolean unlinkable, boolean wildcard, boolean hasTooltip) {
		this(name, item, unlinkable, wildcard, false, hasTooltip);
	}

	PieceType(
			String name,
			Supplier<? extends Item> item,
			boolean unlinkable,
			boolean wildcard,
			boolean passThrough,
			boolean hasTooltip) {
		this.name = name;
		this.item = item;
		this.unlinkable = unlinkable;
		this.wildcard = wildcard;
		this.passThrough = passThrough;
		this.tooltip = hasTooltip ? Component.translatable("gui.fruitfulfun.minigame.piece." + name + ".effect") : null;
		Component displayName = Component.translatable("gui.fruitfulfun.minigame.piece." + name);
		this.displayName = tooltip == null
				? displayName
				: displayName.copy().withStyle(style -> style.withUnderlined(true).withHoverEvent(new HoverEvent.ShowText(tooltip)));
	}

	public int code() {
		return ordinal();
	}

	public boolean unlinkable() {
		return unlinkable;
	}

	public boolean wildcard() {
		return wildcard;
	}

	public boolean passThrough() {
		return passThrough;
	}

	public boolean matches(@Nullable PieceType base) {
		return !unlinkable && (base == null || wildcard || passThrough || base == this);
	}

	public boolean clearsAtBottom() {
		return this == POMEGRANATE;
	}

	public Component displayName() {
		return displayName;
	}

	public @Nullable Component tooltip() {
		return tooltip;
	}

	public ItemStackTemplate template(Piece piece) {
		if (piece.is(LOOTBOX) && piece.data() != null && piece.data().contains("display")) {
			Optional<ItemStackTemplate> display = piece.data().read("display", LycheeCodecs.ITEM_STACK_TEMPLATE);
			if (display.isPresent()) {
				return display.get();
			}
		}
		ItemStackTemplate result = template;
		if (result == null) {
			result = new ItemStackTemplate(item.get());
			template = result;
		}
		return result;
	}

	public ItemStack stack(Piece piece) {
		return template(piece).create();
	}

	public static PieceType byCode(int code) {
		return VALUES[code];
	}
}
