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
import snownee.kiwi.Kiwi;

public class BookFFModuleLoadedCondition extends BookCondition {

	public static final Identifier ID = FruitfulFun.id("module_loaded");
	public static final MapCodec<BookFFModuleLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
			Identifier.CODEC.fieldOf("module").forGetter(condition -> condition.moduleId),
			Codec.BOOL.optionalFieldOf("inverted", false).forGetter(condition -> condition.inverted)
	).apply(instance, BookFFModuleLoadedCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, BookFFModuleLoadedCondition> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC),
			condition -> Optional.ofNullable(condition.tooltip()),
			Identifier.STREAM_CODEC,
			condition -> condition.moduleId,
			ByteBufCodecs.BOOL,
			condition -> condition.inverted,
			BookFFModuleLoadedCondition::new);

	private final Identifier moduleId;
	private final boolean inverted;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BookFFModuleLoadedCondition(Optional<Component> tooltip, Identifier moduleId) {
		this(tooltip, moduleId, false);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BookFFModuleLoadedCondition(Optional<Component> tooltip, Identifier moduleId, boolean inverted) {
		super(tooltip.orElse(null));
		this.moduleId = moduleId;
		this.inverted = inverted;
	}

	@Override
	public BookConditionType<?> type() {
		return BookFFConditionTypes.MODULE_LOADED;
	}

	@Override
	public boolean test(BookConditionContext context, Player player) {
		return testOnLoad();
	}

	@Override
	public boolean testOnLoad() {
		return Kiwi.isLoaded(moduleId) != inverted;
	}

}