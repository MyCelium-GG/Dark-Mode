package my.celium.org.darkmode.client;

/** Background colors for the Mojang loading screen. */
public enum LoadingColor {
    BLACK(0xFF000000),
    CHARCOAL(0xFF17181B),
    MIDNIGHT(0xFF0B1324),
    PLUM(0xFF1A0F24),
    RED(0);

    final int argb;

    LoadingColor(int argb) {
        this.argb = argb;
    }
}
