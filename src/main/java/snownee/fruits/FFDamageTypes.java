package snownee.fruits;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public final class FFDamageTypes {
	public static final ResourceKey<DamageType> EXPLOSION = ResourceKey.create(
			Registries.DAMAGE_TYPE, FruitfulFun.id("explosion"));

	public static boolean isGrenadeExplosion(DamageSource source) {
		return source.is(EXPLOSION);
	}
}
