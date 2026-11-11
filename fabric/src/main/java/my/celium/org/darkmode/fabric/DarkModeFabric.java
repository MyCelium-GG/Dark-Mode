package my.celium.org.darkmode.fabric;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.MissingMycel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric entrypoint. Without Mycel, Dark Mode stays off and tells the player in chat that Mycel is
 * missing; with it, {@link DarkModeFabricStart} starts the mod. This class must not load Mycel.
 */
public final class DarkModeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        if (!FabricLoader.getInstance().isModLoaded("mycel")) {
            DarkGate.markMycelMissing();
            MissingMycel.logOnce();
            return;
        }
        DarkModeFabricStart.start();
    }
}
