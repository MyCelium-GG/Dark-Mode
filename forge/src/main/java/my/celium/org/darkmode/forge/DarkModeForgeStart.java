package my.celium.org.darkmode.forge;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.DarkModeMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * The real start on Forge, only when Mycel is installed. Client classes stay in
 * {@link ClientDarkMode}, because a dedicated server cannot even verify a class that mentions them.
 */
final class DarkModeForgeStart {
    private DarkModeForgeStart() {
    }

    static void start() {
        DarkModeMod.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientDarkMode.register();
            Stats.start();
        }
        DarkGate.open();
    }
}
