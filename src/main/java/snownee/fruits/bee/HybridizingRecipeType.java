package snownee.fruits.bee;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.lychee.LootContextKeys;
import snownee.lychee.util.CommonProxy;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.recipe.BlockKeyableRecipeType;

public class HybridizingRecipeType extends BlockKeyableRecipeType<HybridizingRecipe> {
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
		Stream<HybridizingRecipe> stream = CommonProxy.recipes(this).stream().filter($ -> !$.ghost);
		if (clazz.isAssignableFrom(Comparable.class)) {
			stream = stream.sorted();
		}
		recipes = stream.toList();
		Multimap<Block, HybridizingRecipe> multimap = HashMultimap.create();
		LinkedHashSet<Block> pollenBlocks = new LinkedHashSet<>();
		for (HybridizingRecipe recipe : recipes) {
			recipe.endingStep().stream()
					.map(Identifier::new)
					.map(BuiltInRegistries.BLOCK::get)
					.forEach($ -> multimap.put($, recipe));
			recipe.pollens.stream()
					.map(Identifier::new)
					.map(BuiltInRegistries.BLOCK::get)
					.forEach(pollenBlocks::add);
		}

		for (Map.Entry<Block, Collection<HybridizingRecipe>> entry : multimap.asMap().entrySet()) {
			List<HybridizingRecipe> list = Lists.newArrayList(entry.getValue());
			list.sort(null);
			recipesByBlock.put(entry.getKey(), list);
		}

		for (Block block : pollenBlocks) {
			if (!(block instanceof FruitLeavesBlock) && !block.defaultBlockState().is(BlockTags.FLOWERS)) {
				FruitfulFun.LOGGER.warn("Pollen {} does not have a flower block tag, this may cause issues", block);
			}
		}
	}

	public void onPollinateComplete(Bee bee) {
		BlockPos flowerPos = bee.getSavedFlowerPos();
		ServerLevel level = (ServerLevel) bee.level();
		BlockState blockState = level.getBlockState(flowerPos);
		if (blockState.isAir()) {
			return;
		}
		Pair<LycheeContext, HybridizingRecipe> result = process(bee, flowerPos, blockState);
		if (result == null && blockState.getBlock() instanceof FruitLeavesBlock leaves
				&& blockState.getValue(FruitLeavesBlock.AGE) == FruitLeavesBlock.BLOOMING
				&& leaves.canGrowWithContext(blockState, level, flowerPos)) {
			FruitTreeBlockEntity core = leaves.findCore(level, flowerPos);
			if (core != null) {
				core.consumeLifespan(-2);
			}
			leaves.performBonemeal(level, bee.getRandom(), flowerPos, blockState);
			level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, flowerPos, 0);
		}
	}

	public Pair<LycheeContext, HybridizingRecipe> process(Bee bee, BlockPos flowerPos, BlockState blockState) {
		if (isEmpty()) {
			return null;
		}
		Block block = blockState.getBlock();
		String newPollen = Util.trimRL(BuiltInRegistries.BLOCK.getKey(block));
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		pollens.remove(newPollen);
		pollens.add(newPollen);
		if (!has(blockState)) {
			return null;
		}
		boolean isBigFlowerUpper = false;
		Level level = bee.level();
		if (block instanceof DoublePlantBlock && blockState.hasProperty(DoublePlantBlock.HALF) &&
				blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
			flowerPos = flowerPos.below();
			blockState = level.getBlockState(flowerPos);
			if (block != blockState.getBlock()) {
				return null;
			}
			isBigFlowerUpper = true;
		}
		Pair<LycheeContext, HybridizingRecipe> result = process(bee.level(), blockState, buildContext(bee, flowerPos, blockState));
		if (result != null) {
			level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, flowerPos, 0);
			if (isBigFlowerUpper) {
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, flowerPos.above(), 0);
			}
			if (result.getSecond().resetPollens) {
				pollens.clear();
			}
		}
		return result;
	}

	public Supplier<LycheeContext> buildContext(Bee bee, BlockPos flowerPos, BlockState state) {
		return () -> {
			LycheeContext.Builder<LycheeContext> builder = new LycheeContext.Builder<>(bee.level());
			builder.withRandom(bee.getRandom());
			builder.withParameter(LootContextParams.THIS_ENTITY, bee);
			builder.withParameter(LootContextParams.BLOCK_STATE, state);
			builder.withParameter(LootContextParams.ORIGIN, Vec3.atBottomCenterOf(flowerPos));
			builder.withParameter(LootContextKeys.BLOCK_POS, flowerPos);
			return builder.create(BeeModule.RECIPE_TYPE.get().contextParamSet);
		};
	}
}
