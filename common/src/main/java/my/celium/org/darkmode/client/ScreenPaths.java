package my.celium.org.darkmode.client;

import java.util.List;
import java.util.Locale;

/** Decides from a texture's name whether it is part of a screen we should darken. */
public final class ScreenPaths {
    private static final String VANILLA = "minecraft";
    /** Vanilla's gui atlas also holds HUD and icon art, so only surfaces are listed. */
    private static final List<String> VANILLA_SPRITES = List.of("widget/", "container/", "recipe_book/",
            "advancements/", "popup/", "toast/", "dialog/", "gamemode_switcher/");
    private static final List<String> VANILLA_KEEP = List.of("textures/gui/title/", "textures/gui/presets/",
            "textures/gui/hanging_signs/");
    private static final List<String> MOD_SCREEN_WORDS = List.of("gui/", "screen", "menu", "container");
    private static final List<String> MOD_KEEP_WORDS = List.of("icon", "logo", "title", "item/", "block/", "entity/",
            "particle", "armor");

    private ScreenPaths() {
    }

    /** Whether a standalone texture such as {@code textures/gui/container/generic_54.png} is a screen. */
    public static boolean isScreenTexture(String namespace, String path, boolean modScreens, List<String> skipped) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (namespace.equals(VANILLA)) {
            return lower.startsWith("textures/gui/") && VANILLA_KEEP.stream().noneMatch(lower::startsWith);
        }
        if (!modScreens || skipped.contains(namespace) || MOD_KEEP_WORDS.stream().anyMatch(lower::contains)) {
            return false;
        }
        return lower.startsWith("textures/") && MOD_SCREEN_WORDS.stream().anyMatch(lower::contains);
    }

    /** Whether a sprite on the gui atlas, named for example {@code widget/button}, is a screen surface. */
    public static boolean isScreenSprite(String namespace, String path, boolean modScreens, boolean hud,
            List<String> skipped) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (namespace.equals(VANILLA)) {
            return VANILLA_SPRITES.stream().anyMatch(lower::startsWith) || hud && lower.startsWith("hud/");
        }
        return modScreens && !skipped.contains(namespace) && !lower.contains("icon");
    }
}
