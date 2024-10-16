package snownee.fruits.ritual;

import java.util.Map;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import snownee.lychee.core.LycheeContext;

public class DragonRitualContext extends LycheeContext {
	public final int dragonHeads;

	protected DragonRitualContext(
			RandomSource pRandom,
			Level level,
			Map<LootContextParam<?>, Object> pParams,
			int dragonHeads) {
		super(pRandom, level, pParams);
		this.dragonHeads = dragonHeads;
	}

	public static class Builder extends LycheeContext.Builder<DragonRitualContext> {
		private final int dragonHeads;

		public Builder(Level level, int dragonHeads) {
			super(level);
			this.dragonHeads = dragonHeads;
		}

		@Override
		public DragonRitualContext create(LootContextParamSet pParameterSet) {
			this.beforeCreate(pParameterSet);
			return new DragonRitualContext(random, level, params, dragonHeads);
		}
	}
}
