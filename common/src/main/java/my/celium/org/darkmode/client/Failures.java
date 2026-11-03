package my.celium.org.darkmode.client;

import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;

/**
 * Problems Dark Mode recovers from (a texture it could not recolor stays as it was).
 * Each kind is logged once, and handed to the error reporter when one is installed
 * (FastStats, on the Minecraft lines that ship it).
 */
public final class Failures {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> SEEN = ConcurrentHashMap.newKeySet();
    private static volatile Consumer<Throwable> reporter = ignored -> { };

    private Failures() {
    }

    public static void setReporter(Consumer<Throwable> newReporter) {
        reporter = newReporter;
    }

    public static void report(String what, Throwable error) {
        if (!SEEN.add(what + ':' + error.getClass().getName())) {
            return;
        }
        LOGGER.warn("Dark Mode skipped {}: it could not be recolored", what, error);
        try {
            reporter.accept(error);
        } catch (RuntimeException ignored) {
            // Reporting must never break the game.
        }
    }
}
