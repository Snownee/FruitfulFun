package snownee.fruits.minigame.goal;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.MinigameRule;

public abstract class MinigameGoal {
	private final List<ItemStack> rewards;
	private final List<Pair<MinigameRule, Component>> rules;

	protected MinigameGoal(List<ItemStack> rewards, List<Pair<MinigameRule, Component>> rules) {
		this.rewards = List.copyOf(rewards);
		this.rules = List.copyOf(rules);
	}

	public final List<ItemStack> rewards() {
		return rewards;
	}

	public final List<MinigameRule> rules() {
		return rules.stream().map(Pair::getFirst).toList();
	}

	public final Display display() {
		return new Display(icon(), description(), target(), rewards, rules.stream().map(Pair::getSecond).toList());
	}

	public abstract ItemStack icon();

	public abstract Component description();

	public abstract int target();

	public abstract int advance(ClearResult clear, int progress);

	public static Component pieceName(@Nullable PieceType type) {
		return type == null ? Component.translatable("gui.fruitfulfun.minigame.goal.pieces") : type.displayName();
	}

	public static ItemStack pieceIcon(@Nullable PieceType type, int count) {
		return type == null ? new ItemStack(Items.GOLDEN_APPLE, count) : type.stack(Piece.of(type)).copyWithCount(count);
	}

	public record Display(
			ItemStack icon,
			Component description,
			int target,
			List<ItemStack> rewards,
			List<Component> ruleTexts) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC,
				Display::icon,
				ComponentSerialization.STREAM_CODEC,
				Display::description,
				ByteBufCodecs.VAR_INT,
				Display::target,
				ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.MAX_GOAL_REWARDS)),
				Display::rewards,
				ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.MAX_GOAL_RULES)),
				Display::ruleTexts,
				Display::new);
	}
}
