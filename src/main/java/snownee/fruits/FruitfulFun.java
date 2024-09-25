package snownee.fruits;


import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

public final class FruitfulFun {
	public static final String ID = "fruitfulfun";
	public static final String NAME = "Fruitful Fun";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
