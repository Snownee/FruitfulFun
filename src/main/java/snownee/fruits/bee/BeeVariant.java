package snownee.fruits.bee;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StringRepresentable;
import snownee.fruits.FFRegistries;

public record BeeVariant(Optional<Identifier> texture, RenderType renderType) {
	public static final Codec<BeeVariant> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("texture").forGetter(BeeVariant::texture),
			RenderType.CODEC.optionalFieldOf("render_type", RenderType.CutoutCull).forGetter(BeeVariant::renderType)
	).apply(i, BeeVariant::new));
	public static final Codec<BeeVariant> NETWORK_CODEC = DIRECT_CODEC;
	public static final Codec<Holder<BeeVariant>> CODEC = RegistryFixedCodec.create(FFRegistries.BEE_VARIANT_KEY);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BeeVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FFRegistries.BEE_VARIANT_KEY);

	public enum RenderType implements StringRepresentable {
		CutoutCull("cutout_cull"), Cutout("cutout"), Translucent("translucent");

		public static final Codec<RenderType> CODEC = StringRepresentable.fromEnum(RenderType::values);

		private final String name;

		RenderType(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
