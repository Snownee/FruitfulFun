package snownee.fruits.bee;

import java.util.Optional;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;

public class BeeVariants {
	public static final ResourceKey<BeeVariant> NORMAL = createKey(FruitfulFun.id("normal"));
	public static final ResourceKey<BeeVariant> PINK = createKey(FruitfulFun.id("pink"));
	public static final ResourceKey<BeeVariant> WITHER = createKey(FruitfulFun.id("wither"));
	public static final ResourceKey<BeeVariant> GHOST = createKey(FruitfulFun.id("ghost"));

	public static ResourceKey<BeeVariant> createKey(Identifier id) {
		return ResourceKey.create(FFRegistries.BEE_VARIANT_KEY, id);
	}

	public static void bootstrap(BootstrapContext<BeeVariant> context) {
		context.register(NORMAL, new BeeVariant(Optional.empty(), BeeVariant.RenderType.CutoutCull));
		register(context, PINK, "pink", BeeVariant.RenderType.CutoutCull);
		register(context, WITHER, "wither", BeeVariant.RenderType.Cutout);
		register(context, GHOST, "ghost", BeeVariant.RenderType.Translucent);
	}

	public static void register(
			BootstrapContext<BeeVariant> context,
			ResourceKey<BeeVariant> name,
			String texture,
			BeeVariant.RenderType renderType) {
		register(context, name, FruitfulFun.id(texture), renderType);
	}

	public static void register(
			BootstrapContext<BeeVariant> context,
			ResourceKey<BeeVariant> name,
			Identifier texture,
			BeeVariant.RenderType renderType) {
		context.register(name, new BeeVariant(Optional.of(texture), renderType));
	}
}
