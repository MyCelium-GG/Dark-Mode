package my.celium.org.darkmode.fabric;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.DarkModeMod;

/** The real start on Fabric, only when Mycel is installed. */
final class DarkModeFabricStart {
    private DarkModeFabricStart() {
    }

    static void start() {
        DarkModeMod.init();
        Stats.start();
        DarkGate.open();
    }
}
