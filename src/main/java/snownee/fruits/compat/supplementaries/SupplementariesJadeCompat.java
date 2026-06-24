package snownee.fruits.compat.supplementaries;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;

public class SupplementariesJadeCompat {
	public static void register(IWailaCommonRegistration registration) {
//		InspectorBlockProvider provider = new InspectorBlockProvider($ -> {
//			return SupplementariesCompat.getTargetEntity($.getBlockEntity());
//		});
//		registration.registerBlockDataProvider(provider, CageBlockTile.class);
//		registration.registerBlockDataProvider(provider, JarBlockTile.class);
	}

	public static void registerClient(IWailaClientRegistration registration) {
//		InspectorProvider provider = new InspectorProvider();
//		registration.registerBlockComponent(provider, CageBlock.class);
//		registration.registerBlockComponent(provider, JarBlock.class);
	}
}
