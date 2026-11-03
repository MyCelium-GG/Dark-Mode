package my.celium.org.darkmode.stats;

import dev.faststats.ErrorTracker;
import dev.faststats.Metrics;
import dev.faststats.data.Metric;
import java.util.List;
import my.celium.org.darkmode.DarkModeMod;
import my.celium.org.darkmode.client.Failures;

/**
 * FastStats for Dark Mode: anonymous settings counts and Dark Mode's own errors, only after the
 * player agreed on FastStats' consent screen. Built into the 26.2 and 26.3 jars only, because each
 * FastStats jar is tied to one exact Minecraft version and the 26.1 jar runs on three.
 *
 * Players can change their choice any time in config/faststats/config.properties.
 */
public final class DarkModeStats {
    /** Project token from faststats.dev. Empty means FastStats stays off. */
    public static final String TOKEN = "";

    private static final String OWN_PACKAGE = "my.celium.org.darkmode.";

    private DarkModeStats() {
    }

    public static boolean configured() {
        return !TOKEN.isBlank();
    }

    /** Reports only errors that went through Dark Mode's code, with personal details blanked. */
    public static ErrorTracker errorTracker() {
        ErrorTracker tracker = ErrorTracker.contextAware()
                .addExclusionRule((thread, error) -> !involvesDarkMode(error))
                .anonymize("(?i)(?:/home/|/Users/|[a-z]:\\\\Users\\\\)[^/\\\\\\s]+", "[user home]")
                .anonymize("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}", "[uuid]");
        Failures.setReporter(error -> tracker.trackError(error));
        return tracker;
    }

    static boolean involvesDarkMode(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            for (StackTraceElement frame : t.getStackTrace()) {
                if (frame.getClassName().startsWith(OWN_PACKAGE)) {
                    return true;
                }
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }

    /** What players choose, so the defaults and themes can follow what people actually use. */
    public static Metrics metrics(Metrics.Factory factory) {
        return factory
                .addMetric(Metric.bool("enabled", () -> DarkModeMod.enabled.get()))
                .addMetric(Metric.string("theme", () -> DarkModeMod.theme().name().toLowerCase()))
                .addMetric(Metric.number("brightness", () -> DarkModeMod.brightness.get()))
                .addMetric(Metric.string("loading_screen", () -> DarkModeMod.loadingColor().name().toLowerCase()))
                .addMetric(Metric.bool("mod_screens", () -> DarkModeMod.modScreens.get()))
                .addMetric(Metric.bool("hud", () -> DarkModeMod.hud.get()))
                .addMetric(Metric.bool("drawn_backgrounds", () -> DarkModeMod.drawnBackgrounds.get()))
                .addMetric(Metric.bool("readable_text", () -> DarkModeMod.readableText.get()))
                // Mods whose screens players exclude: the ones Dark Mode still gets wrong.
                .addMetric(Metric.stringArray("skipped_mods", () -> skippedMods().toArray(String[]::new)))
                .create();
    }

    private static List<String> skippedMods() {
        return DarkModeMod.skippedMods.get().stream()
                .map(id -> id.trim().toLowerCase())
                .filter(id -> id.matches("[a-z0-9_.-]{1,64}"))
                .distinct()
                .limit(50)
                .toList();
    }
}
