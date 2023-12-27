package snownee.fruits.util;

import static snownee.fruits.CoreModule.*;
import static snownee.fruits.cherry.CherryModule.*;

import java.util.function.Supplier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import snownee.fruits.CoreModule;
import snownee.fruits.Hooks;
import snownee.fruits.cherry.client.SlidingDoorRenderer;
import snownee.fruits.cherry.client.particle.PetalParticle;

public class ClientProxy implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(CoreModule.SLIDING_DOOR.getOrCreate(), SlidingDoorRenderer::new);

		WoodType.register(CoreModule.CITRUS_WOOD_TYPE);

		Supplier<BlockColor> oakBlockColor = ColorProviderUtil.delegate(Blocks.OAK_LEAVES);
		ColorProviderRegistry.BLOCK.register((state, world, pos, i) ->
				{
					if (i == 0) {
						return oakBlockColor.get().getColor(Blocks.OAK_LEAVES.defaultBlockState(), world, pos, i);
					}
					if (i == 1) {
						if (CITRON_LEAVES.is(state))
							return 0xDDCC58;
						if (GRAPEFRUIT_LEAVES.is(state))
							return 0xF4502B;
						if (LEMON_LEAVES.is(state))
							return 0xEBCA4B;
						if (LIME_LEAVES.is(state))
							return 0xCADA76;
						if (TANGERINE_LEAVES.is(state))
							return 0xF08A19;
						if (ORANGE_LEAVES.is(state))
							return 0xF08A19;
						if (POMELO_LEAVES.is(state))
							return 0xF7F67E;
						if (APPLE_LEAVES.is(state))
							return 0xFC1C2A;
					}
					return -1;
				},
				TANGERINE_LEAVES.getOrCreate(),
				LIME_LEAVES.getOrCreate(),
				CITRON_LEAVES.getOrCreate(),
				POMELO_LEAVES.getOrCreate(),
				ORANGE_LEAVES.getOrCreate(),
				LEMON_LEAVES.getOrCreate(),
				GRAPEFRUIT_LEAVES.getOrCreate(),
				APPLE_LEAVES.getOrCreate());

		ItemStack oakLeaves = new ItemStack(Items.OAK_LEAVES);
		Supplier<ItemColor> itemColor = ColorProviderUtil.delegate(Items.OAK_LEAVES);
		ColorProviderRegistry.ITEM.register((stack, i) -> itemColor.get().getColor(oakLeaves, i),
				TANGERINE_LEAVES.get(),
				LIME_LEAVES.get(),
				CITRON_LEAVES.get(),
				POMELO_LEAVES.get(),
				ORANGE_LEAVES.get(),
				LEMON_LEAVES.get(),
				GRAPEFRUIT_LEAVES.get(),
				APPLE_LEAVES.get());

		if (Hooks.cherry) {
			WoodType.register(REDLOVE_WOOD_TYPE);

			ParticleFactoryRegistry.getInstance().register(PETAL_CHERRY.getOrCreate(), PetalParticle.Factory::new);
			ParticleFactoryRegistry.getInstance().register(PETAL_REDLOVE.getOrCreate(), PetalParticle.Factory::new);

			Supplier<BlockColor> birchBlockColor = ColorProviderUtil.delegate(Blocks.BIRCH_LEAVES);
			ColorProviderRegistry.BLOCK.register((state, world, pos, i) -> {
				if (i == 1) {
					return 0xC22626;
				}
				if (i == 2) {
					return birchBlockColor.get().getColor(Blocks.BIRCH_LEAVES.defaultBlockState(), world, pos, i);
				}
				return -1;
			}, REDLOVE_LEAVES.getOrCreate());

			ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
				if (i != 0) {
					if (blockAndTintGetter == null || blockPos == null) {
						return GrassColor.getDefaultColor();
					}
					return BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos);
				}
				return -1;
			}, PEACH_PINK_PETALS.getOrCreate());
		}
	}
}
