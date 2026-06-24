package snownee.fruits.gadget.datagen;

import java.util.List;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import snownee.fruits.gadget.BuzzyPowerStorage;
import snownee.fruits.gadget.ScentedCandleBlockEntity;

public class SetBuzzyPowerFunction extends LootItemConditionalFunction {
	public static final MapCodec<? extends SetBuzzyPowerFunction> CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i).apply(
			i,
			SetBuzzyPowerFunction::new));

	public SetBuzzyPowerFunction(List<LootItemCondition> predicates) {
		super(predicates);
	}

	@Override
	public MapCodec<? extends SetBuzzyPowerFunction> codec() {
		return CODEC;
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		if (!(context.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof ScentedCandleBlockEntity be)) {
			return stack;
		}
		BuzzyPowerStorage power = be.power();
		if (power.isEmpty()) {
			return stack;
		}
		int count = stack.getCount();
		if (count > 1) {
			float maxLife = power.maxLife() / count;
			float life = power.life() / count;
			float red = power.red() / count;
			float green = power.green() / count;
			float blue = power.blue() / count;
			power = new BuzzyPowerStorage(maxLife, (int) life, red, green, blue);
		}
		BuzzyPowerStorage.write(stack, power);
		return stack;
	}

	@Override
	public Set<ContextKey<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.BLOCK_ENTITY);
	}

	public static LootItemConditionalFunction.Builder<?> create() {
		return LimitCount.simpleBuilder(SetBuzzyPowerFunction::new);
	}
}
