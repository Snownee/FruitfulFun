package snownee.fruits.compat.jade;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.bee.Bee;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class BeeDebugProvider implements IServerDataProvider<EntityAccessor> {
	public static final Identifier UID = FruitfulFun.id("bee_debug");

	@Override
	public void appendServerData(CompoundTag data, EntityAccessor accessor) {
		Bee bee = (Bee) accessor.getEntity();
		BeeAttributes attributes = BeeAttributes.of(bee);
		data.putBoolean("Trusted", attributes.trusts(accessor.getPlayer().getUUID()));
//		data.putBoolean("Rolling", bee.isRolling());
		data.store("BeeAttributes", BeeAttributes.CODEC, attributes);
	}

	@Override
	public Identifier getUid() {
		return UID;
	}

	public static class Client extends BeeDebugProvider implements IEntityComponentProvider {

		@Override
		public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
			if (Hooks.bee && accessor.getPlayer().isHolding(BeeModule.INSPECTOR.get())) {
				return;
			}
			CompoundTag data = accessor.getServerData();
			if (data.getBooleanOr("Trusted", false)) {
				tooltip.add(Component.literal("Trusted"));
			}
//		if (data.getBoolean("Rolling")) {
//			tooltip.add(Component.literal("Rolling"));
//		}

			BeeAttributes attributes = data.read("BeeAttributes", BeeAttributes.CODEC).orElse(null);
			if (attributes == null) {
				return;
			}
			List<String> genes = Lists.newArrayList();
			attributes.getGenes().getLoci().forEach((allele, locus) -> {
				genes.add(allele.getDisplayName(locus.getHigh()).getString());
				genes.add(allele.getDisplayName(locus.getLow()).getString());
			});
			tooltip.add(Component.literal(String.join(" ", genes)));
			List<String> traits = Lists.newArrayList();
			attributes.getGenes().getTraits().forEach(trait -> traits.add(trait.name()));
			if (!traits.isEmpty()) {
				traits.sort(String::compareTo);
				tooltip.add(Component.literal(String.join(" ", traits)));
			}
		}

		@Override
		public boolean isRequired() {
			return true;
		}
	}

}
