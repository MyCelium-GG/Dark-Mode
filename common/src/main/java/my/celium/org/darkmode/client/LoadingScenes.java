package my.celium.org.darkmode.client;

import my.celium.org.darkmode.DarkModeMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** The pictures the loading screen can show behind the Mojang logo. */
public final class LoadingScenes {
    private LoadingScenes() {
    }

    /**
     * Draws the chosen picture over the whole screen, faded by {@code alpha}. Returns whether
     * anything was drawn; plain colours draw nothing here.
     */
    public static boolean draw(GuiGraphicsExtractor gfx, float alpha) {
        if (!DarkModeMod.isModEnabled()) {
            return false;
        }
        int width = gfx.guiWidth();
        int height = gfx.guiHeight();
        try {
            return switch (DarkModeMod.loadingColor()) {
                case CHRISTMAS -> {
                    WinterLoading.draw(gfx, width, height, alpha);
                    yield true;
                }
                case CUSTOM -> CustomLoading.draw(gfx, width, height, alpha);
                default -> false;
            };
        } catch (RuntimeException e) {
            Failures.report("loading screen", e);
            return false;
        }
    }
}
