package snownee.fruits.gadget.datagen;

import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import snownee.fruits.gadget.BuzzyPowerStorage;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.ScentedCandleBlockEntity;

public class SetBuzzyPowerFunction extends LootItemConditionalFunction {
	public SetBuzzyPowerFunction(LootItemCondition[] lootItemConditions) {
		super(lootItemConditions);
	}

	@Override
	public MapCodec<? extends LootItemConditionalFunction> codec() {
		return null;
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		if (!(context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof ScentedCandleBlockEntity be)) {
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
	public LootItemFunctionType getType() {
		return GadgetModule.SET_BUZZY_POWER.get();
	}

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.BLOCK_ENTITY);
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer<SetBuzzyPowerFunction> {
		@Override
		public SetBuzzyPowerFunction deserialize(
				JsonObject object,
				JsonDeserializationContext deserializationContext,
				LootItemCondition[] conditions) {
			return new SetBuzzyPowerFunction(conditions);
		}
	}

	public static LootItemConditionalFunction.Builder<?> create() {
		return LimitCount.simpleBuilder(SetBuzzyPowerFunction::new);
	}
}
