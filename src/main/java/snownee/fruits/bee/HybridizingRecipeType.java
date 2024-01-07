package snownee.fruits.bee;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.util.Util;
import snownee.lychee.LycheeLootContextParams;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.recipe.type.BlockKeyRecipeType;
import snownee.lychee.util.CommonProxy;
import snownee.lychee.util.Pair;

public class HybridizingRecipeType extends BlockKeyRecipeType<LycheeContext, HybridizingRecipe> {
	public HybridizingRecipeType(String name, Class<HybridizingRecipe> clazz, @Nullable LootContextParamSet paramSet) {
		super(name, clazz, paramSet);
	}

	public static void removeOverflownPollens(Bee bee) {
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		if (pollens.size() > 3) {
			int toRemove = pollens.size() - 3;
			while (toRemove-- > 0) {
				pollens.remove(0);
			}
		}
	}

	@Override
	public void buildCache() {
		super.buildCache();
		this.recipesByBlock.clear();
		this.anyBlockRecipes.clear();
		Stream<HybridizingRecipe> stream = CommonProxy.recipes(this).stream().filter($ -> !$.ghost);
		if (clazz.isAssignableFrom(Comparable.class)) {
			stream = stream.sorted();
		}
		recipes = stream.toList();
		Multimap<Block, HybridizingRecipe> multimap = HashMultimap.create();
		for (HybridizingRecipe recipe : recipes) {
			recipe.endingStep().stream()
					.map(ResourceLocation::new)
					.map(BuiltInRegistries.BLOCK::get)
					.forEach($ -> multimap.put($, recipe));
		}

		for (Map.Entry<Block, Collection<HybridizingRecipe>> entry : multimap.asMap().entrySet()) {
			List<HybridizingRecipe> list = Lists.newArrayList(entry.getValue());
			list.sort(null);
			recipesByBlock.put(entry.getKey(), list);
		}
	}

	public void onPollinateComplete(Bee bee) {
		if (isEmpty()) {
			return;
		}
		BlockPos flowerPos = bee.getSavedFlowerPos();
		if (flowerPos == null) {
			return;
		}
		Level level = bee.level();
		BlockState state = level.getBlockState(flowerPos);
		if (state.isAir()) {
			return;
		}
		Block block = state.getBlock();
		String newPollen = Util.trimRL(BuiltInRegistries.BLOCK.getKey(block));
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		pollens.remove(newPollen);
		pollens.add(newPollen);
		if (!has(state)) {
			return;
		}
		boolean isBigFlowerUpper = false;
		if (block instanceof DoublePlantBlock && state.hasProperty(DoublePlantBlock.HALF) && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
			flowerPos = flowerPos.below();
			state = level.getBlockState(flowerPos);
			if (block != state.getBlock()) {
				return;
			}
			isBigFlowerUpper = true;
		}
		Pair<LycheeContext, HybridizingRecipe> result = process(bee.level(), state, buildContext(bee, flowerPos, state));
		if (result != null) {
			level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, flowerPos, 0);
			if (isBigFlowerUpper) {
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, flowerPos.above(), 0);
			}
			pollens.clear();
		}
	}

	public Supplier<LycheeContext> buildContext(Bee bee, BlockPos flowerPos, BlockState state) {
		return () -> {
			LycheeContext.Builder<LycheeContext> builder = new LycheeContext.Builder<>(bee.level());
			builder.withRandom(bee.getRandom());
			builder.withParameter(LootContextParams.THIS_ENTITY, bee);
			builder.withParameter(LootContextParams.BLOCK_STATE, state);
			builder.withParameter(LootContextParams.ORIGIN, Vec3.atBottomCenterOf(flowerPos));
			builder.withParameter(LycheeLootContextParams.BLOCK_POS, flowerPos);
			return builder.create(BeeModule.RECIPE_TYPE.get().contextParamSet);
		};
	}
}
