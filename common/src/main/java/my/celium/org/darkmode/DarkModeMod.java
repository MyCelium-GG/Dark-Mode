package my.celium.org.darkmode;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import my.celium.org.Mycel;
import my.celium.org.config.ConfigBuilder;
import my.celium.org.config.ConfigValue;
import my.celium.org.config.MycelConfig;
import my.celium.org.darkmode.client.Darkness;
import my.celium.org.darkmode.client.LoadingColor;
import my.celium.org.darkmode.client.Theme;
import my.celium.org.metadata.ModMetadata;

/**
 * Dark Mode: recolors screen textures as they load, so screens from any mod
 * turn dark without a per-mod resource pack, and recolors what screens draw
 * in code. Client-side only; the mixins are in a client-only list.
 */
public final class DarkModeMod {
    public static final String ID = "darkmode";

    public static final ModMetadata META = ModMetadata.builder(ID, "Dark Mode", "1.1.0")
            .author("MyCelium")
            .website("https://www.curseforge.com/minecraft/mc-mods/dark-mode")
            .updateUrl("https://wiki.mycelium-gg.org/updates/darkmode.json")
            .build();

    public static ConfigValue<Boolean> enabled;
    public static ConfigValue<String> theme;
    public static ConfigValue<Integer> brightness;
    public static ConfigValue<String> loadingScreen;
    public static ConfigValue<Boolean> modScreens;
    public static ConfigValue<Boolean> hud;
    public static ConfigValue<Boolean> drawnBackgrounds;
    public static ConfigValue<Boolean> readableText;
    public static ConfigValue<List<String>> skippedMods;
    public static MycelConfig config;

    private DarkModeMod() {
    }

    /** Called once from each loader's entrypoint, only when Mycel is installed. */
    public static void init() {
        Mycel.initialize();
        Mycel.registerMod(META);

        ConfigBuilder builder = Mycel.config(META);
        enabled = builder.booleanValue("enabled", true,
                "Turn dark screens on or off.");
        theme = builder.stringValue("theme", Theme.NEUTRAL.name(),
                "The tint of dark screens: NEUTRAL, MIDNIGHT, FOREST, PLUM, EMBER or WINTER (snow falls behind menus).");
        brightness = builder.intValue("brightness", 30, 8, 60,
                "How bright dark screens are, in percent of the normal brightness.");
        loadingScreen = builder.stringValue("loadingScreen", LoadingColor.BLACK.name(),
                "Mojang loading screen: BLACK, CHARCOAL, MIDNIGHT, PLUM, RED (normal), CHRISTMAS (a winter landscape) or CUSTOM (config/darkmode/loading.png).");
        modScreens = builder.booleanValue("modScreens", true,
                "Also darken screens from other mods.");
        hud = builder.booleanValue("hud", false,
                "Also darken the hotbar and other parts of the HUD.");
        drawnBackgrounds = builder.booleanValue("drawnBackgrounds", true,
                "Darken light backgrounds that screens draw in code instead of with a texture.");
        readableText = builder.booleanValue("readableText", true,
                "Brighten dark text so it stays readable on dark screens.");
        skippedMods = builder.stringListValue("skippedMods", List.of(),
                "Mod ids whose screens stay as they are, for example create.");
        config = builder.build();

        Consumer<Object> texturesChanged = ignored -> Darkness.markTexturesChanged();
        enabled.onChange(texturesChanged::accept);
        theme.onChange(texturesChanged::accept);
        brightness.onChange(texturesChanged::accept);
        modScreens.onChange(texturesChanged::accept);
        hud.onChange(texturesChanged::accept);
        skippedMods.onChange(texturesChanged::accept);
    }

    public static Theme theme() {
        return named(Theme.class, theme.get(), Theme.NEUTRAL);
    }

    public static LoadingColor loadingColor() {
        return named(LoadingColor.class, loadingScreen.get(), LoadingColor.BLACK);
    }

    private static <E extends Enum<E>> E named(Class<E> type, String name, E fallback) {
        try {
            return Enum.valueOf(type, name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static boolean isModEnabled() {
        return enabled != null && enabled.get() && Mycel.isEnabled(ID);
    }
}
