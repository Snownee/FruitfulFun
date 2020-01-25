package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import snownee.fruits.Fruits;
import snownee.fruits.Hook;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        Fruits.logger.info("Invoking Mixin Connector");
        Mixins.addConfiguration("assets/fruittrees/fruittrees.mixins.json");
        Hook.mixin = true;
    }

}
