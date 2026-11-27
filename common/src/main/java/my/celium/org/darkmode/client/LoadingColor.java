package my.celium.org.darkmode.client;

/** What the Mojang loading screen shows: a plain colour, a Christmas scene or your own image. */
public enum LoadingColor {
    BLACK(0xFF000000),
    CHARCOAL(0xFF17181B),
    MIDNIGHT(0xFF0B1324),
    PLUM(0xFF1A0F24),
    RED(0),
    /** The Christmas village photo, see {@link WinterLoading}. */
    CHRISTMAS_VILLAGE(WinterLoading.SKY_TOP),
    /** The tree with presents at sunset, see {@link WinterLoading}. */
    CHRISTMAS_GIFTS(WinterLoading.SKY_TOP),
    /** The first pixel-art winter night, shown as "Christmas (legacy)". Keeps its name so old configs still pick it. */
    CHRISTMAS(WinterLoading.SKY_TOP),
    /** Your own image, see {@link CustomLoading}; black around it while it loads. */
    CUSTOM(0xFF000000);

    final int argb;

    LoadingColor(int argb) {
        this.argb = argb;
    }
}
