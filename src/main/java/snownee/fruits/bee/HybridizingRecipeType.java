package snownee.fruits.bee;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
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
import snownee.kiwi.util.KUtil;
import snownee.lychee.LootContextKeys;
import snownee.lychee.context.LootParamsContext;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;
import snownee.lychee.util.recipe.LycheeRecipeType;

public class HybridizingRecipeType extends LycheeRecipeType<HybridizingRecipe> {
	protected final Map<Block, List<RecipeHolder<HybridizingRecipe>>> recipesByBlock = Maps.newHashMap();

	public HybridizingRecipeType(String name, Class<HybridizingRecipe> clazz, @Nullable ContextKeySet paramSet) {
		super(name, clazz, paramSet);
	}

	public static void removeOverflownPollens(Bee bee) {
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		if (pollens.size() > 3) {
			int toRemove = pollens.size() - 3;
			while (toRemove-- > 0) {
				pollens.removeFirst();
			}
		}
	}

	@Override
	public void refreshCache(RecipeMap recipeMap) {
		super.refreshCache(recipeMap);
		this.recipesByBlock.clear();
		Multimap<Block, RecipeHolder<HybridizingRecipe>> multimap = HashMultimap.create();
		LinkedHashSet<Block> pollenBlocks = new LinkedHashSet<>();
		for (RecipeHolder<HybridizingRecipe> recipe : recipes) {
			recipe.value().endingStep().stream()
					.map(Identifier::parse)
					.map(BuiltInRegistries.BLOCK::getValue)
					.forEach($ -> multimap.put($, recipe));
			recipe.value().pollens().stream()
					.map(Identifier::parse)
					.map(BuiltInRegistries.BLOCK::getValue)
					.forEach(pollenBlocks::add);
		}

		Comparator<RecipeHolder<HybridizingRecipe>> comparator = comparator();
		for (Map.Entry<Block, Collection<RecipeHolder<HybridizingRecipe>>> entry : multimap.asMap().entrySet()) {
			List<RecipeHolder<HybridizingRecipe>> list = Lists.newArrayList(entry.getValue());
			list.sort(comparator);
			recipesByBlock.put(entry.getKey(), list);
		}

		for (Block block : pollenBlocks) {
			if (!(block instanceof FruitLeavesBlock) && !block.defaultBlockState().is(BlockTags.FLOWERS)) {
				FruitfulFun.LOGGER.warn("Pollen {} does not have a flower block tag, this may cause issues", block);
			}
		}
	}

	@Override
	public Comparator<RecipeHolder<HybridizingRecipe>> comparator() {
		return super.comparator().thenComparing(recipe -> recipe.value().pollens().size());
	}

	public void onPollinateComplete(Bee bee) {
		BlockPos flowerPos = bee.getSavedFlowerPos();
		if (flowerPos == null) {
			return;
		}
		ServerLevel level = (ServerLevel) bee.level();
		BlockState blockState = level.getBlockState(flowerPos);
		if (blockState.isAir()) {
			return;
		}
		Optional<RecipeHolder<HybridizingRecipe>> result = process(bee, flowerPos, blockState);
		if (result.isEmpty() && blockState.getBlock() instanceof FruitLeavesBlock leaves
				&& blockState.getValue(FruitLeavesBlock.AGE) == FruitLeavesBlock.BLOOMING
				&& leaves.canGrowWithContext(blockState, level, flowerPos)) {
			FruitTreeBlockEntity core = leaves.findCore(level, flowerPos);
			if (core != null) {
				core.consumeLifespan(-2);
			}
			leaves.performBonemeal(level, bee.getRandom(), flowerPos, blockState);
			level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, flowerPos, 0);
		}
	}

	public Optional<RecipeHolder<HybridizingRecipe>> process(Bee bee, BlockPos flowerPos, BlockState blockState) {
		if (isEmpty()) {
			return Optional.empty();
		}
		Block block = blockState.getBlock();
		String newPollen = KUtil.trimRL(BuiltInRegistries.BLOCK.getKey(block));
		BeeAttributes attributes = BeeAttributes.of(bee);
		List<String> pollens = attributes.getPollens();
		pollens.remove(newPollen);
		pollens.add(newPollen);
		if (!has(blockState)) {
			return Optional.empty();
		}
		boolean isBigFlowerUpper = false;
		Level level = bee.level();
		if (block instanceof DoublePlantBlock && blockState.hasProperty(DoublePlantBlock.HALF) &&
				blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
			flowerPos = flowerPos.below();
			blockState = level.getBlockState(flowerPos);
			if (block != blockState.getBlock()) {
				return Optional.empty();
			}
			isBigFlowerUpper = true;
		}
		LycheeContext ctx = new LycheeContext();
		ctx.put(LycheeContextKey.RANDOM, bee.getRandom());
		LootParamsContext lootParams = ctx.initLootParams(this);
		lootParams.set(LootContextParams.THIS_ENTITY, bee);
		lootParams.set(LootContextParams.BLOCK_STATE, blockState);
		lootParams.set(LootContextParams.ORIGIN, Vec3.atBottomCenterOf(flowerPos));
		lootParams.set(LootContextKeys.BLOCK_POS, flowerPos);
		Optional<RecipeHolder<HybridizingRecipe>> result = findFirst(ctx, level);
		if (result.isPresent()) {
			ctx.put(result.get());
			level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, flowerPos, 0);
			if (isBigFlowerUpper) {
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, flowerPos.above(), 0);
			}
			if (result.get().value().resetPollens()) {
				pollens.clear();
			}
		}
		return result;
	}

	public boolean has(BlockState state) {
		return recipesByBlock.containsKey(state.getBlock());
	}
}
