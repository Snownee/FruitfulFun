package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.List;
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

public final class PieceType {
	private static final List<PieceType> REGISTRY = new ArrayList<>();

	public static final PieceType ORANGE = builder("orange", CoreModule.ORANGE).noTooltip().build();
	public static final PieceType LEMON = builder("lemon", CoreModule.LEMON).noTooltip().build();
	public static final PieceType CHERRY = builder("cherry", CherryModule.CHERRY).noTooltip().build();
	public static final PieceType CHORUS = builder("chorus", () -> Items.CHORUS_FRUIT).noTooltip().build();
	public static final PieceType LOOTBOX = builder("lootbox", () -> Items.BUNDLE).unlinkable().build();
	public static final PieceType REDLOVE = builder("redlove", CherryModule.REDLOVE).build();
	public static final PieceType POMEGRANATE = builder("pomegranate", PomegranateModule.POMEGRANATE_ITEM).unlinkable().build();
	public static final PieceType GOLDEN_APPLE = builder("golden_apple", () -> Items.GOLDEN_APPLE).wildcard().build();
	public static final PieceType GOLDEN_CARROT = builder("golden_carrot", () -> Items.GOLDEN_CARROT).converter().build();
	public static final PieceType BEE = builder("bee", () -> Items.BEE_SPAWN_EGG).wildcard().passiveImmune().build();
	public static final PieceType BEEHIVE = builder("beehive", () -> Items.BEE_NEST).passThrough().passiveImmune().build();
	public static final PieceType LARGE_ORANGE = builder("large_orange", CoreModule.ORANGE).base(ORANGE).build();
	public static final PieceType LARGE_LEMON = builder("large_lemon", CoreModule.LEMON).base(LEMON).build();
	public static final PieceType LARGE_CHERRY = builder("large_cherry", CherryModule.CHERRY).base(CHERRY).build();
	public static final PieceType LARGE_CHORUS = builder("large_chorus", () -> Items.CHORUS_FRUIT).base(CHORUS).build();
	public static final PieceType LARGE_GOLDEN_APPLE = builder("large_golden_apple", () -> Items.GOLDEN_APPLE)
			.base(GOLDEN_APPLE)
			.wildcard()
			.build();

	private static final PieceType[] VALUES = REGISTRY.toArray(PieceType[]::new);

	public static final WeightedList<PieceType> FRUITS = WeightedList.<PieceType>builder()
			.add(ORANGE, 20)
			.add(LEMON, 20)
			.add(CHERRY, 20)
			.add(CHORUS, 20)
			.add(GOLDEN_APPLE, 10)
			.build();
	public static final StreamCodec<ByteBuf, PieceType> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(PieceType::byCode, PieceType::code);

	private final int code;
	private final Supplier<? extends Item> item;
	private final boolean unlinkable;
	private final boolean wildcard;
	private final boolean converter;
	private final boolean passThrough;
	private final boolean passiveImmune;
	private final PieceType base;
	private final Component displayName;
	private final @Nullable Component tooltip;
	private volatile @Nullable ItemStackTemplate template;

	private PieceType(Builder builder) {
		this.code = REGISTRY.size();
		this.item = builder.item;
		String name = builder.name;
		this.unlinkable = builder.unlinkable;
		this.wildcard = builder.wildcard;
		this.converter = builder.converter;
		this.passThrough = builder.passThrough;
		this.passiveImmune = builder.passiveImmune;
		this.base = builder.base == null ? this : builder.base;
		this.tooltip = builder.noTooltip
				? null
				: Component.translatable("gui.fruitfulfun.minigame.piece." + name + ".effect");
		Component base = Component.translatable("gui.fruitfulfun.minigame.piece." + name);
		this.displayName = tooltip == null
				? base
				: base.copy().withStyle(style -> style.withUnderlined(true).withHoverEvent(new HoverEvent.ShowText(tooltip)));
		REGISTRY.add(this);
	}

	public static Builder builder(String name, Supplier<? extends Item> item) {
		return new Builder(name, item);
	}

	public static final class Builder {
		private final String name;
		private final Supplier<? extends Item> item;
		private boolean unlinkable;
		private boolean wildcard;
		private boolean converter;
		private boolean passThrough;
		private boolean passiveImmune;
		private @Nullable PieceType base;
		private boolean noTooltip;

		private Builder(String name, Supplier<? extends Item> item) {
			this.name = name;
			this.item = item;
		}

		public Builder unlinkable() {
			this.unlinkable = true;
			return this;
		}

		public Builder wildcard() {
			this.wildcard = true;
			return this;
		}

		public Builder converter() {
			this.converter = true;
			return this;
		}

		public Builder passThrough() {
			this.passThrough = true;
			return this;
		}

		public Builder passiveImmune() {
			this.passiveImmune = true;
			return this;
		}

		public Builder base(PieceType base) {
			this.base = base;
			return this;
		}

		public Builder noTooltip() {
			this.noTooltip = true;
			return this;
		}

		public PieceType build() {
			return new PieceType(this);
		}
	}

	public int code() {
		return code;
	}

	public boolean unlinkable() {
		return unlinkable;
	}

	public boolean wildcard() {
		return wildcard;
	}

	public boolean converter() {
		return converter;
	}

	public boolean passThrough() {
		return passThrough;
	}

	public boolean passiveImmune() {
		return passiveImmune;
	}

	public PieceType base() {
		return base;
	}

	public boolean isLarge() {
		return base != this;
	}

	public boolean sameFamily(PieceType other) {
		return this == other || base == other;
	}

	public boolean matches(@Nullable PieceType base) {
		return !unlinkable && (base == null || wildcard || converter || passThrough || base == this.base);
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
