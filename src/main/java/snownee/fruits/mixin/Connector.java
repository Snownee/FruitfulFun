package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import snownee.fruits.FruitsMod;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        FruitsMod.logger.info("Invoking Mixin Connector");
        Mixins.addConfiguration("assets/fruittrees/fruittrees.mixins.json");
        FruitsMod.mixin = true;
    }

}
