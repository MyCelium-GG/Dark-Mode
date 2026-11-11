package my.celium.org.darkmode.neoforge;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.MissingMycel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge entrypoint: server-safe bootstrap. Without Mycel, Dark Mode stays off and tells the
 * player in chat that Mycel is missing; with it, {@link DarkModeNeoForgeStart} starts the mod. This
 * class must not load Mycel.
 */
@Mod("darkmode")
public final class DarkModeNeoForge {
    public DarkModeNeoForge(@SuppressWarnings("unused") IEventBus modBus,
            @SuppressWarnings("unused") ModContainer container) {
        if (!ModList.get().isLoaded("mycel")) {
            DarkGate.markMycelMissing();
            MissingMycel.logOnce();
            return;
        }
        DarkModeNeoForgeStart.start();
    }
}
