package snownee.fruits.client;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record Head() implements ConditionalItemModelProperty {
	public static final MapCodec<Head> MAP_CODEC = MapCodec.unit(new Head());

	@Override
	public boolean get(
			final ItemStack itemStack,
			final @Nullable ClientLevel level,
			final @Nullable LivingEntity owner,
			final int seed,
			final ItemDisplayContext displayContext) {
		return displayContext == ItemDisplayContext.HEAD;
	}

	@Override
	public MapCodec<Head> type() {
		return MAP_CODEC;
	}
}
