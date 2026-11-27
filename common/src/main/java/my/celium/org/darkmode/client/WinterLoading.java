package my.celium.org.darkmode.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * The Christmas loading screens: winter scenes built in Minecraft and photographed. Each fills the
 * window like a wallpaper and a light snowfall drifts over it. Client only.
 */
final class WinterLoading {
    /** The top of the legacy picture's night sky; the plain background behind every scene uses it. */
    static final int SKY_TOP = 0xFF141433;

    /** One picture, its size in pixels, and how much it is dimmed so the white Mojang logo stays readable. */
    enum Scene {
        /** A snowy village with a candy cane lamp and a tall tree, shot with shaders. */
        VILLAGE("village", 1920, 1046, 0x40000000),
        /** A big tree with presents under it at sunset, shot with shaders. */
        GIFTS("gifts", 1920, 1046, 0x48000000),
        /**
         * The first Christmas screen (1.1.0): a decorated tree, log cabin, snow golems and a frozen
         * pond at sunset, turned into 480 x 270 pixel art and graded for a winter night.
         */
        LEGACY("christmas", 480, 270, 0);

        final Identifier picture;
        final int width;
        final int height;
        final int dim;

        Scene(String file, int width, int height, int dim) {
            this.picture = Identifier.fromNamespaceAndPath("darkmode", "textures/loading/" + file + ".png");
            this.width = width;
            this.height = height;
            this.dim = dim;
        }
    }

    private WinterLoading() {
    }

    /** Fills the whole {@code width} x {@code height} screen (GUI pixels) with {@code scene}, faded by {@code alpha}. */
    static void draw(GuiGraphicsExtractor gfx, Scene scene, int width, int height, float alpha) {
        // Cover the screen: fill both ways and cut what sticks out, keeping the middle.
        double screen = width / (double) height;
        double picture = scene.width / (double) scene.height;
        int regionW = scene.width;
        int regionH = scene.height;
        if (picture > screen) {
            regionW = (int) Math.round(scene.height * screen);
        } else {
            regionH = (int) Math.round(scene.width / screen);
        }
        float u = (scene.width - regionW) / 2f;
        float v = (scene.height - regionH) / 2f;
        PixelArt.image(gfx, scene.picture, 0, 0, width, height, u, v, regionW, regionH, scene.width, scene.height, alpha);
        if (scene.dim != 0) {
            gfx.fill(0, 0, width, height, PixelArt.fade(scene.dim, alpha));
        }
        snowfall(gfx, width, height, alpha, Math.max(1, Math.round(width / (float) regionW)));
    }

    /** Flakes drifting down, sized like the picture's pixels (one GUI pixel for the photos). */
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
