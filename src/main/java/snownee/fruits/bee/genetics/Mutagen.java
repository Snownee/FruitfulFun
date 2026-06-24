package snownee.fruits.bee.genetics;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.util.ClientProxy;
import snownee.kiwi.loader.Platform;

public record Mutagen(String type, int color) implements TooltipProvider {
	public static final Codec<Mutagen> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("type").forGetter(Mutagen::type),
			Codec.INT.fieldOf("color").forGetter(Mutagen::color)
	).apply(instance, Mutagen::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, Mutagen> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			$ -> $.type,
			ByteBufCodecs.INT,
			$ -> $.color,
			Mutagen::new);
	public static final Mutagen IMPERFECT = new Mutagen("imperfect", 0xF3DCEB);

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {

	}

	public boolean isImperfect() {
		return this == IMPERFECT || type.equals("imperfect");
	}

	public String getClientName() {
		if (Platform.isPhysicalClient() && ClientProxy.getPlayer() != null) {
			return FFPlayer.of(ClientProxy.getPlayer()).fruits$getGeneName(type);
		}
		return type;
	}
}
