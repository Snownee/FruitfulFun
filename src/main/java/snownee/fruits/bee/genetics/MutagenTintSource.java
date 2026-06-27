package snownee.fruits.bee.genetics;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.bee.BeeModule;

public class MutagenTintSource implements ItemTintSource {
	public static final MutagenTintSource INSTANCE = new MutagenTintSource();
	public static final MapCodec<MutagenTintSource> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
		return itemStack.getOrDefault(BeeModule.MUTAGEN_CONTENT.get(), Mutagen.IMPERFECT).color() | 0xFF000000;
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return CODEC;
	}
}
