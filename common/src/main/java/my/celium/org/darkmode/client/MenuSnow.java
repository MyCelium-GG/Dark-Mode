package my.celium.org.darkmode.client;

import my.celium.org.darkmode.DarkModeMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Gentle snowfall behind the buttons of every menu while the Winter theme is on. */
public final class MenuSnow {
    private MenuSnow() {
    }

    public static void draw(GuiGraphicsExtractor gfx, int width, int height) {
        if (!DarkModeMod.isModEnabled() || DarkModeMod.theme() != Theme.WINTER || width <= 4 || height <= 4) {
            return;
        }
        double t = System.currentTimeMillis() / 1000.0;
        int count = Math.max(30, width * height / 1400);
        for (int i = 0; i < count; i++) {
            int n = PixelArt.hash(i * 31 + 11);
            double speed = 8 + n % 12;
            double y = (n % height + t * speed) % height;
            double sway = Math.sin(t * (0.5 + (n % 5) * 0.15) + i) * 4;
            int x = (int) Math.floorMod((long) (PixelArt.hash(i * 17 + 5) % width + sway), (long) width);
            int size = n % 5 == 0 ? 2 : 1;
            gfx.fill(x, (int) y, x + size, (int) y + size, PixelArt.fade(0xFFFFFFFF, 0.25f + (n % 4) * 0.1f));
        }
    }
}
