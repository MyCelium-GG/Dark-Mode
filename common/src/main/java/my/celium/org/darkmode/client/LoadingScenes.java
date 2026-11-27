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
                case CHRISTMAS_VILLAGE -> winter(gfx, WinterLoading.Scene.VILLAGE, width, height, alpha);
                case CHRISTMAS_GIFTS -> winter(gfx, WinterLoading.Scene.GIFTS, width, height, alpha);
                case CHRISTMAS -> winter(gfx, WinterLoading.Scene.LEGACY, width, height, alpha);
                case CUSTOM -> CustomLoading.draw(gfx, width, height, alpha);
                default -> false;
            };
        } catch (RuntimeException e) {
            Failures.report("loading screen", e);
            return false;
        }
    }

    private static boolean winter(GuiGraphicsExtractor gfx, WinterLoading.Scene scene, int width, int height, float alpha) {
        WinterLoading.draw(gfx, scene, width, height, alpha);
        return true;
    }
}
