package snownee.fruits.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.Mod;

@Mod(FruitfulFun.ID)
public class CommonProxy implements ModInitializer {
	public static boolean isCurativeItem(MobEffectInstance effectInstance, ItemStack stack) {
		return stack.is(Items.MILK_BUCKET);
	}

	public static boolean isFakePlayer(Entity entity) {
		return entity instanceof FakePlayer;
	}

	public static Packet<ClientGamePacketListener> getAddEntityPacket(Entity entity) {
		return new ClientboundAddEntityPacket(entity);
	}

	public static void maybeGrowCrops(ServerLevel world, BlockPos pos, BlockState state, boolean defaultResult, Runnable defaultAction) {
		if (defaultResult) {
			defaultAction.run();
		}
	}

	@Override
	public void onInitialize() {
	}
}
