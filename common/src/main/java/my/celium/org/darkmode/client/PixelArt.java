package my.celium.org.darkmode.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/**
 * Paints pixel art once into a texture and then draws it as one image, scaled up so each art pixel
 * covers {@code scale} x {@code scale} screen pixels. Painting thousands of tiny rectangles every
 * frame is slow; painting them once and drawing one image is not. Client only.
 */
final class PixelArt {
    /** Something pixel art can be painted on, in art pixels: an image, or the screen for things that move. */
    interface Paint {
        void fill(int x0, int y0, int x1, int y1, int argb);

        /** A box whose colour runs from {@code top} to {@code bottom}. */
        default void gradient(int x0, int y0, int x1, int y1, int top, int bottom) {
            int rows = Math.max(1, y1 - y0);
            for (int y = y0; y < y1; y++) {
                float f = rows == 1 ? 0f : (y - y0) / (float) (rows - 1);
                fill(x0, y, x1, y + 1, lerp(top, bottom, f));
            }
        }
    }

    /** Paints straight to the screen, scaled and faded, for the few things that move. */
    record Screen(GuiGraphicsExtractor gfx, int scale, float alpha) implements Paint {
        @Override
        public void fill(int x0, int y0, int x1, int y1, int argb) {
            gfx.fill(x0 * scale, y0 * scale, x1 * scale, y1 * scale, fade(argb, alpha));
        }
    }

    private static final int MAX_CACHED = 8;
    private static final Map<String, Baked> CACHE = new LinkedHashMap<>(8, 0.75f, true);
    private static int next;

    private record Baked(Identifier id, int w, int h) {
    }

    private PixelArt() {
    }

    /**
     * Draws the art named {@code key}, {@code w} x {@code h} art pixels, at the top-left of the
     * screen, scaled by {@code scale} and faded by {@code alpha}. Paints it first if needed.
     */
    static void draw(GuiGraphicsExtractor gfx, String key, int w, int h, int scale, float alpha, Consumer<Paint> painter) {
        if (w <= 0 || h <= 0 || alpha <= 0f) {
            return;
        }
        Baked baked = CACHE.get(key);
        if (baked == null || baked.w != w || baked.h != h) {
            baked = bake(key, w, h, painter);
        }
        gfx.blit(RenderPipelines.GUI_TEXTURED, baked.id, 0, 0, 0f, 0f, w * scale, h * scale, w, h, w, h,
                ARGB.white(alpha));
    }

    /** Draws an already registered texture: {@code region} of it stretched over the box, faded. */
    static void image(GuiGraphicsExtractor gfx, Identifier id, int x, int y, int w, int h,
            float u, float v, int regionW, int regionH, int texW, int texH, float alpha) {
        gfx.blit(RenderPipelines.GUI_TEXTURED, id, x, y, u, v, w, h, regionW, regionH, texW, texH, ARGB.white(alpha));
    }

    private static Baked bake(String key, int w, int h, Consumer<Paint> painter) {
        NativeImage image = new NativeImage(w, h, true);
        painter.accept(new Image(image));
        Identifier id = Identifier.fromNamespaceAndPath("darkmode", "art/" + next++);
        Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "Dark Mode art", image));
        Baked baked = new Baked(id, w, h);
        Baked old = CACHE.put(key, baked);
        if (old != null) {
            release(old);
        }
        while (CACHE.size() > MAX_CACHED) {
            var eldest = CACHE.entrySet().iterator();
            release(eldest.next().getValue());
            eldest.remove();
        }
        return baked;
    }

    private static void release(Baked baked) {
        Minecraft.getInstance().getTextureManager().release(baked.id);
    }

    /** Paints into an image, blending see-through colours the way the screen would. */
    private record Image(NativeImage image) implements Paint {
        @Override
        public void fill(int x0, int y0, int x1, int y1, int argb) {
            int ax = Math.max(0, x0);
            int ay = Math.max(0, y0);
            int bx = Math.min(image.getWidth(), x1);
            int by = Math.min(image.getHeight(), y1);
            for (int y = ay; y < by; y++) {
                for (int x = ax; x < bx; x++) {
                    put(x, y, argb);
                }
            }
        }

        private void put(int x, int y, int src) {
            int sa = src >>> 24;
            if (sa == 0) {
                return;
            }
            if (sa == 255) {
                image.setPixel(x, y, src);
                return;
            }
            int dst = image.getPixel(x, y);
            int da = dst >>> 24;
            float a = sa / 255f;
            float b = da / 255f * (1f - a);
            float out = a + b;
            int r = Math.round(((src >> 16 & 0xFF) * a + (dst >> 16 & 0xFF) * b) / out);
            int g = Math.round(((src >> 8 & 0xFF) * a + (dst >> 8 & 0xFF) * b) / out);
            int bl = Math.round(((src & 0xFF) * a + (dst & 0xFF) * b) / out);
            image.setPixel(x, y, Math.round(out * 255f) << 24 | r << 16 | g << 8 | bl);
        }
    }

    static int lerp(int from, int to, float f) {
        int a = Math.round((from >>> 24) + ((to >>> 24) - (from >>> 24)) * f);
        int r = Math.round((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int g = Math.round((from >> 8 & 0xFF) + ((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int b = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * f);
        return a << 24 | r << 16 | g << 8 | b;
    }

    /** {@code argb} with its alpha multiplied by {@code alpha} (0 to 1). */
    static int fade(int argb, float alpha) {
        int a = Math.round((argb >>> 24) * Math.clamp(alpha, 0f, 1f));
        return (a << 24) | (argb & 0xFFFFFF);
    }

    static int hash(int x) {
        int h = x * 0x45D9F3B;
        h = (h ^ (h >>> 16)) * 0x45D9F3B;
        h ^= h >>> 16;
        return h & 0x7FFFFFFF;
    }
}
