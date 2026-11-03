package my.celium.org.darkmode.neoforge;

import dev.faststats.neoforge.NeoForgeContext;
import my.celium.org.darkmode.DarkModeMod;
import my.celium.org.darkmode.stats.DarkModeStats;

/** Starts FastStats (it shows its consent screen, submits and shuts down by itself). Client only. */
final class Stats {
    private static NeoForgeContext context;

    private Stats() {
    }

    static void start() {
        if (!DarkModeStats.configured()) {
            return;
        }
        context = new NeoForgeContext.Factory(DarkModeMod.ID, DarkModeStats.TOKEN)
                .requireConsent()
                .errorTrackerService(DarkModeStats.errorTracker())
                .metrics(DarkModeStats::metrics)
                .create();
    }
}
