package my.celium.org.darkmode.fabric;

import dev.faststats.fabric.FabricContext;
import my.celium.org.darkmode.DarkModeMod;
import my.celium.org.darkmode.stats.DarkModeStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/** Starts FastStats (it shows its consent screen, submits and shuts down by itself). */
final class Stats {
    private static FabricContext context;

    private Stats() {
    }

    static void start() {
        if (!DarkModeStats.configured() || FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }
        context = new FabricContext.Factory(DarkModeMod.ID, DarkModeStats.TOKEN)
                .requireConsent()
                .errorTrackerService(DarkModeStats.errorTracker())
                .metrics(DarkModeStats::metrics)
                .create();
    }
}
