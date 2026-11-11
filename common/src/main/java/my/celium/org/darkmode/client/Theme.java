package my.celium.org.darkmode.client;

/** Tints for dark screens, as multipliers on red, green and blue. */
public enum Theme {
    NEUTRAL(1.0f, 1.0f, 1.0f),
    MIDNIGHT(0.72f, 0.86f, 1.28f),
    FOREST(0.76f, 1.14f, 0.84f),
    PLUM(1.04f, 0.74f, 1.22f),
    EMBER(1.24f, 0.88f, 0.70f),
    /** Icy blue, with snow falling behind every menu (see {@link MenuSnow}). */
    WINTER(0.80f, 1.0f, 1.26f);

    final float red;
    final float green;
    final float blue;

    Theme(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
