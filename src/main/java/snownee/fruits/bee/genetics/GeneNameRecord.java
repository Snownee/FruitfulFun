package snownee.fruits.bee.genetics;

import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GeneNameRecord(String code, String name, String desc) {
	public GeneNameRecord {
		Objects.requireNonNull(name);
		Objects.requireNonNull(desc);
	}

	public static final Codec<GeneNameRecord> CODEC = Codec.STRING.listOf(3, 3)
			.xmap(
					list -> new GeneNameRecord(list.getFirst(), list.get(1), list.get(2)),
					record -> List.of(record.code(), record.name(), record.desc()));

	public static final Codec<List<GeneNameRecord>> LIST_CODEC = CODEC.listOf();

	public static final StreamCodec<ByteBuf, GeneNameRecord> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			GeneNameRecord::code,
			ByteBufCodecs.STRING_UTF8,
			GeneNameRecord::name,
			ByteBufCodecs.STRING_UTF8,
			GeneNameRecord::desc,
			GeneNameRecord::new);
}
