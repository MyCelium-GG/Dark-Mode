package my.celium.org.darkmode.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * The Christmas loading screen: a winter scene built in Minecraft (a decorated tree with presents,
 * a log cabin with smoke, snow golems, lanterns and a frozen pond at sunset), photographed, turned
 * into 480 x 270 pixel art and colour graded for a winter night. It fills the window like a
 * wallpaper with sharp pixels, and a light snowfall drifts over it. The Mojang logo sits in the
 * sky and the progress bar on the frozen pond. Client only.
 */
final class WinterLoading {
    /** The top of the picture's night sky; the plain background behind it uses it too. */
    static final int SKY_TOP = 0xFF141433;

    private static final Identifier PICTURE = Identifier.fromNamespaceAndPath("darkmode", "textures/loading/christmas.png");
    private static final int PICTURE_W = 480;
    private static final int PICTURE_H = 270;

    private WinterLoading() {
    }

    /** Fills the whole {@code width} x {@code height} screen (GUI pixels), faded by {@code alpha}. */
    static void draw(GuiGraphicsExtractor gfx, int width, int height, float alpha) {
        // Cover the screen: fill both ways and cut what sticks out, keeping the middle.
        double screen = width / (double) height;
        double picture = PICTURE_W / (double) PICTURE_H;
        int regionW = PICTURE_W;
        int regionH = PICTURE_H;
        if (picture > screen) {
            regionW = (int) Math.round(PICTURE_H * screen);
        } else {
            regionH = (int) Math.round(PICTURE_W / screen);
        }
        float u = (PICTURE_W - regionW) / 2f;
        float v = (PICTURE_H - regionH) / 2f;
        PixelArt.image(gfx, PICTURE, 0, 0, width, height, u, v, regionW, regionH, PICTURE_W, PICTURE_H, alpha);
        snowfall(gfx, width, height, alpha, Math.max(1, Math.round(width / (float) regionW)));
    }

    /** Flakes drifting down, sized like the picture's pixels. */
    private static void snowfall(GuiGraphicsExtractor gfx, int width, int height, float alpha, int pixel) {
        double t = System.currentTimeMillis() / 1000.0;
        int w = width / pixel;
        int h = height / pixel;
        int count = Math.max(40, w * h / 900);
        for (int i = 0; i < count; i++) {
            int n = PixelArt.hash(i * 31 + 7);
            double speed = 6 + n % 10;
            double y = (n % Math.max(1, h) + t * speed) % Math.max(1, h);
            double sway = Math.sin(t * (0.5 + (n % 5) * 0.15) + i) * 3;
            int x = (int) Math.floorMod((long) (PixelArt.hash(i * 17 + 3) % Math.max(1, w) + sway), (long) Math.max(1, w));
            int size = n % 4 == 0 ? 2 : 1;
            int px = x * pixel;
            int py = (int) y * pixel;
            gfx.fill(px, py, px + size * pixel, py + size * pixel,
                    PixelArt.fade(0xFFFFFFFF, alpha * (0.35f + (n % 4) * 0.12f)));
        }
    }
}
