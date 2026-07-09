package snownee.fruits.bee;

import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record BoundEntity(UUID uuid, EntityType<?> entityType, Component name) implements TooltipProvider {
	public static final Codec<BoundEntity> CODEC = RecordCodecBuilder.create(i -> i.group(
			UUIDUtil.CODEC.fieldOf("uuid").forGetter(BoundEntity::uuid),
			EntityType.CODEC.fieldOf("entity_type").forGetter(BoundEntity::entityType),
			ComponentSerialization.CODEC.fieldOf("name").forGetter(BoundEntity::name)
	).apply(i, BoundEntity::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoundEntity> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			BoundEntity::uuid,
			EntityType.STREAM_CODEC,
			BoundEntity::entityType,
			ComponentSerialization.STREAM_CODEC,
			BoundEntity::name,
			BoundEntity::new);

	public BoundEntity(Entity entity) {
		this(entity.getUUID(), entity.getType(), entity.getName());
	}

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		consumer.accept(Component.translatable("tip.fruitfulfun.boundEntity.status", name.copy().withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));
	}
}
