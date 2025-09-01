package snownee.fruits.gadget.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import snownee.fruits.gadget.BeeSwarm;

public class BeeSwarmRenderer extends EntityRenderer<BeeSwarm> {
	public BeeSwarmRenderer(EntityRendererProvider.Context context) {
		super(context);
		shadowRadius = 0;
	}

	@Override
	public ResourceLocation getTextureLocation(BeeSwarm entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
