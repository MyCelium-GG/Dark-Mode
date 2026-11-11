package my.celium.org.darkmode.forge;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.MissingMycel;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Forge entrypoint: server-safe bootstrap. Without Mycel, Dark Mode stays off and tells the player
 * in chat that Mycel is missing; with it, {@link DarkModeForgeStart} starts the mod. This class
 * must not load Mycel.
 */
@Mod("darkmode")
public final class DarkModeForge {
    public DarkModeForge(@SuppressWarnings("unused") FMLJavaModLoadingContext context) {
        if (!ModList.isLoaded("mycel")) {
            DarkGate.markMycelMissing();
            MissingMycel.logOnce();
            return;
        }
        DarkModeForgeStart.start();
    }
}
