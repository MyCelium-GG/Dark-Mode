package my.celium.org.darkmode.forge;

import my.celium.org.darkmode.DarkModeMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Forge entrypoint: server-safe bootstrap. Client classes stay in {@link ClientDarkMode},
 * because a dedicated server cannot even verify a class that mentions them.
 */
@Mod(DarkModeMod.ID)
public final class DarkModeForge {
    public DarkModeForge(@SuppressWarnings("unused") FMLJavaModLoadingContext context) {
        DarkModeMod.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientDarkMode.register();
        }
    }
}
