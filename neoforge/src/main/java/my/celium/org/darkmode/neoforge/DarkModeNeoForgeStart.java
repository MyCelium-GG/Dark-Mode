package my.celium.org.darkmode.neoforge;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.DarkModeMod;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * The real start on NeoForge, only when Mycel is installed. Client classes stay in
 * {@link ClientDarkMode}, because a dedicated server cannot even verify a class that mentions them.
 */
final class DarkModeNeoForgeStart {
    private DarkModeNeoForgeStart() {
    }

    static void start() {
        DarkModeMod.init();
        if (FMLEnvironment.getDist().isClient()) {
            ClientDarkMode.register();
            Stats.start();
        }
        DarkGate.open();
    }
}
