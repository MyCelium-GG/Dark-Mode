package my.celium.org.darkmode.neoforge;

import my.celium.org.darkmode.DarkModeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * NeoForge entrypoint: server-safe bootstrap. Client classes stay in {@link ClientDarkMode},
 * because a dedicated server cannot even verify a class that mentions them.
 */
@Mod(DarkModeMod.ID)
public final class DarkModeNeoForge {
    public DarkModeNeoForge(@SuppressWarnings("unused") IEventBus modBus,
            @SuppressWarnings("unused") ModContainer container) {
        DarkModeMod.init();
        if (FMLEnvironment.getDist().isClient()) {
            ClientDarkMode.register();
            Stats.start();
        }
    }
}
