package snownee.fruits.pomegranate;

import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.lychee.LycheeRegistries;

@KiwiModule("dev")
public class PomegranateModule extends AbstractModule {
	public static final KiwiGO<FFExplodeAction.Type> EXPLODE = go(FFExplodeAction.Type::new, () -> LycheeRegistries.POST_ACTION);
}
