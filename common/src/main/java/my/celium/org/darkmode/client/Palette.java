package my.celium.org.darkmode.client;

/**
 * The color math. Greys get darker by a fixed factor, which keeps the light
 * and dark edges of slots and buttons the right way round. Saturated colors
 * such as hearts and arrows stay as they are.
 */
public final class Palette {
    /** Above this saturation a color is left completely alone. */
    private static final float MAX_SATURATION = 0.42f;
    /** Below this saturation a color counts as fully grey. */
    private static final float GREY_SATURATION = 0.14f;
    /** Below this brightness text is too dark to read on a dark screen. */
    private static final int DARK_TEXT = 0x78;
    /** Below this brightness a drawn background is already dark. */
    private static final int LIGHT_FILL = 0x58;
    private static final int OPAQUE_FILL = 0xC0;
    /** Below this perceived brightness colored text is too dark, which covers pure dark blue. */
    private static final int DARK_COLOR = 0x50;
    private static final int LIGHT_COLOR = 0xD8;
    private static final float WHITE_MIX = 0.3f;

    private Palette() {
    }

    /** Darkens one texture pixel; the alpha never changes. */
    public static int darken(int argb, Theme theme, int brightnessPercent) {
        int alpha = argb >>> 24;
        if (alpha == 0) {
            return argb;
        }
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        float saturation = max == 0 ? 0 : (max - min) / (float) max;
        if (saturation >= MAX_SATURATION) {
            return argb;
        }
        float amount = saturation <= GREY_SATURATION
                ? 1
                : (MAX_SATURATION - saturation) / (MAX_SATURATION - GREY_SATURATION);
        float factor = brightnessPercent / 100f;
        return alpha << 24
                | mix(red, factor * theme.red, amount) << 16
                | mix(green, factor * theme.green, amount) << 8
                | mix(blue, factor * theme.blue, amount);
    }

    private static int mix(int channel, float factor, float amount) {
        float result = channel + (channel * factor - channel) * amount;
        return Math.max(0, Math.min(255, Math.round(result)));
    }

    /** The new color for a background drawn in code; thin lines and see-through fills stay. */
    public static int darkenFill(int argb, int width, int height, Theme theme, int brightnessPercent) {
        if (width <= 2 || height <= 2 || argb >>> 24 < OPAQUE_FILL || brightness(argb) < LIGHT_FILL) {
            return argb;
        }
        return darken(argb, theme, brightnessPercent);
    }

    /** Brightens dark text. Greys become light grey; colors keep their hue. */
    public static int readableText(int argb) {
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        boolean colored = max > 0 && (max - min) / (float) max >= MAX_SATURATION;
        if (colored && (red * 3 + green * 6 + blue) / 10 >= DARK_COLOR || !colored && max >= DARK_TEXT) {
            return argb;
        }
        if (colored) {
            // Mixed toward white too: a pure dark blue stays hard to read if only scaled up.
            float scale = LIGHT_COLOR / (float) max;
            return argb & 0xFF000000 | tinted(red, scale) << 16 | tinted(green, scale) << 8 | tinted(blue, scale);
        }
        int light = 0xE6 - max / 2;
        return argb & 0xFF000000 | light << 16 | light << 8 | light;
    }

    private static int tinted(int channel, float scale) {
        return Math.round(channel * scale + (0xFF - channel * scale) * WHITE_MIX);
    }

    private static int brightness(int argb) {
        return Math.max((argb >> 16) & 0xFF, Math.max((argb >> 8) & 0xFF, argb & 0xFF));
    }
}
