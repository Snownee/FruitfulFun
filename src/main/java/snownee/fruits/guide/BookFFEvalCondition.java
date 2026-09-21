package snownee.fruits.guide;

import java.util.Optional;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.shadowed.com.ezylang.evalex.EvaluationException;
import snownee.kiwi.shadowed.com.ezylang.evalex.Expression;
import snownee.kiwi.shadowed.com.ezylang.evalex.parser.ParseException;
import snownee.kiwi.util.KEval;

public class BookFFEvalCondition extends BookCondition {

	public static final Identifier ID = FruitfulFun.id("eval");
	public static final MapCodec<BookFFEvalCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
			Codec.STRING.fieldOf("expression").forGetter(condition -> condition.expression)
	).apply(instance, (tooltip, expression) -> new BookFFEvalCondition(tooltip.orElse(null), expression)));
	public static final StreamCodec<RegistryFriendlyByteBuf, BookFFEvalCondition> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC),
			condition -> Optional.ofNullable(condition.tooltip()),
			ByteBufCodecs.STRING_UTF8,
			condition -> condition.expression,
			(tooltip, expression) -> new BookFFEvalCondition(tooltip.orElse(null), expression));

	private final String expression;

	public BookFFEvalCondition(Component tooltip, String expression) {
		super(tooltip);
		this.expression = expression;
	}

	@Override
	public BookConditionType<?> type() {
		return BookFFConditionTypes.EVAL;
	}

	@Override
	public boolean test(BookConditionContext context, Player player) {
		return testOnLoad();
	}

	@Override
	public boolean testOnLoad() {
		try {
			return Boolean.TRUE.equals(new Expression(expression, KEval.config()).evaluate().getBooleanValue());
		} catch (ParseException | EvaluationException e) {
			throw new RuntimeException("Failed to evaluate condition: " + expression, e);
		}
	}

}
