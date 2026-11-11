package my.celium.org.darkmode;

/**
 * Whether Dark Mode is running. The mixins check this before anything else, so they never touch a
 * Mycel class when Mycel is not installed. This class must never reference Mycel or {@link DarkModeMod}.
 */
public final class DarkGate {
    private static volatile boolean active;
    private static volatile boolean mycelMissing;

    private DarkGate() {
    }

    /** True once the mod started with Mycel installed. */
    public static boolean active() {
        return active;
    }

    /** True when the loader reported that Mycel is not installed. */
    public static boolean mycelMissing() {
        return mycelMissing;
    }

    public static void open() {
        active = true;
    }

    public static void markMycelMissing() {
        mycelMissing = true;
    }
}
