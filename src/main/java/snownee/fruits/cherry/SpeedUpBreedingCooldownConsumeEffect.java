package snownee.fruits.cherry;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public class SpeedUpBreedingCooldownConsumeEffect implements ConsumeEffect {
	public static final SpeedUpBreedingCooldownConsumeEffect INSTANCE = new SpeedUpBreedingCooldownConsumeEffect();
	public static final MapCodec<SpeedUpBreedingCooldownConsumeEffect> CODEC = MapCodec.unit(() -> INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, SpeedUpBreedingCooldownConsumeEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<SpeedUpBreedingCooldownConsumeEffect> getType() {
		return CherryModule.SPEED_UP_BREEDING_COOLDOWN.get();
	}

	@Override
	public boolean apply(Level level, ItemStack stack, LivingEntity user) {
		if (!(user instanceof Animal animal)) {
			return false;
		}
		int age = animal.getAge();
		int skip = Math.max(age / 3, 600);
		animal.setAge(Math.max(0, age - skip));
		return true;
	}
}
