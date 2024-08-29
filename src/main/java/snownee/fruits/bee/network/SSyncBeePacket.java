package snownee.fruits.bee.network;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;
import snownee.kiwi.network.KPacketTarget;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "sync_bee", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSyncBeePacket extends PacketHandler {
	public static SSyncBeePacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer serverPlayer) {
		int id = buf.readVarInt();
		ItemStack saddle = buf.readItem();
		List<UUID> trusted = buf.readList(FriendlyByteBuf::readUUID);
		String texture = buf.readUtf();
		List<String> traits = buf.readList(FriendlyByteBuf::readUtf);
		long mutagenEndsIn = buf.readLong();
		return executor.apply(() -> {
			Entity entity = Objects.requireNonNull(Minecraft.getInstance().level).getEntity(id);
			if (entity instanceof Bee) {
				BeeAttributes attributes = BeeAttributes.of(entity);
				attributes.setSaddle(saddle);
				attributes.setTrusted(trusted);
				if (texture.isEmpty()) {
					attributes.setTexture(null);
				} else {
					attributes.setTexture(ResourceLocation.tryParse(texture));
				}
				attributes.getGenes().setTraits(traits.stream()
						.map(Trait.REGISTRY::get)
						.filter(Objects::nonNull)
						.toList());
				attributes.setMutagenEndsIn(mutagenEndsIn, entity.level().getGameTime());
			}
		});
	}

	public static void send(Bee bee) {
		I.send(KPacketTarget.tracking(bee), putData(bee));
	}

	public static void send(Bee bee, ServerPlayer player) {
		I.send(player, putData(bee));
	}

	private static Consumer<FriendlyByteBuf> putData(Bee bee) {
		BeeAttributes attributes = BeeAttributes.of(bee);
		return buf -> {
			buf.writeVarInt(bee.getId());
			buf.writeItem(attributes.getSaddle());
			buf.writeCollection(attributes.getTrusted(), FriendlyByteBuf::writeUUID);
			ResourceLocation texture = attributes.getTexture();
			buf.writeUtf(texture == null ? "" : texture.toString());
			buf.writeCollection(attributes.getGenes().getTraits().stream().map(Trait::name).toList(), FriendlyByteBuf::writeUtf);
			buf.writeLong(attributes.getMutagenEndsIn());
		};
	}
}
