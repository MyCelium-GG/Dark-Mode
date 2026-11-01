package my.celium.org.darkmode.neoforge;

import my.celium.org.darkmode.client.DarkModeScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

final class ClientDarkMode {
    private ClientDarkMode() {
    }

    static void register() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (ModContainer ignored, Screen parent) -> new DarkModeScreen(parent));
    }
}
