package snownee.fruits;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public final class FFDamageTypes {
	public static final ResourceKey<DamageType> EXPLOSION = ResourceKey.create(
			Registries.DAMAGE_TYPE, FruitfulFun.id("explosion"));
	public static final ResourceKey<DamageType> PLAYER_EXPLOSION = ResourceKey.create(
			Registries.DAMAGE_TYPE, FruitfulFun.id("player_explosion"));

	public static DamageSource explosion(DamageSources sources, @Nullable Entity entity, @Nullable Entity causingEntity) {
		return sources.source(causingEntity != null && entity != null ? PLAYER_EXPLOSION : EXPLOSION, entity, causingEntity);
	}

	public static boolean isGrenadeExplosion(DamageSource source) {
		return source.is(EXPLOSION) || source.is(PLAYER_EXPLOSION);
	}
}
