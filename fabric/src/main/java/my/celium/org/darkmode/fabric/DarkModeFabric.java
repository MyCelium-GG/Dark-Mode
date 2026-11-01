package my.celium.org.darkmode.fabric;

import my.celium.org.darkmode.DarkModeMod;
import net.fabricmc.api.ModInitializer;

public final class DarkModeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DarkModeMod.init();
    }
}
