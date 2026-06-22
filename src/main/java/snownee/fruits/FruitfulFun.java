package snownee.fruits;


import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.Identifier;

public final class FruitfulFun {
	public static final String ID = "fruitfulfun";
	public static final String NAME = "Fruitful Fun";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
