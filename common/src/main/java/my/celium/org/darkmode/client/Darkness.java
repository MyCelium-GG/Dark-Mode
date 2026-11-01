package my.celium.org.darkmode.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.IntSupplier;
import my.celium.org.client.MycelConfigScreen;
import my.celium.org.client.MycelScreens;
import my.celium.org.darkmode.DarkModeMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

/** What the mixins call. Settings can still be unset while the game starts, so each entry checks. */
public final class Darkness {
    private static final Logger LOGGER = LogUtils.getLogger();
    /** Remembers that we, not the player, turned on vanilla's Monochrome Logo option. */
    private static final String OPTION_MARKER = "darkmode-monochrome-logo";
    private static volatile boolean texturesChanged;

    private Darkness() {
    }

    public static void markTexturesChanged() {
        texturesChanged = true;
    }

    /** Vanilla's gui atlas, and the ones mods such as JEI make for their own screens. */
    public static boolean isGuiAtlas(Identifier location) {
        return location.getPath().contains("gui");
    }

    /** Standalone screen textures. */
    public static void textureLoaded(Identifier location, NativeImage image) {
        if (DarkModeMod.isModEnabled() && ScreenPaths.isScreenTexture(location.getNamespace(), location.getPath(),
                DarkModeMod.modScreens.get(), skipped())) {
            recolor(image);
        }
    }

    /** Sprites of the gui atlas. */
    public static void spriteLoaded(Identifier name, NativeImage image) {
        if (DarkModeMod.isModEnabled() && ScreenPaths.isScreenSprite(name.getNamespace(), name.getPath(),
                DarkModeMod.modScreens.get(), DarkModeMod.hud.get(), skipped())) {
            recolor(image);
        }
    }

    private static List<String> skipped() {
        return DarkModeMod.skippedMods.get();
    }

    private static void recolor(NativeImage image) {
        if (image.format() != NativeImage.Format.RGBA) {
            return;
        }
        Theme theme = DarkModeMod.theme();
        int brightness = DarkModeMod.brightness.get();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setPixel(x, y, Palette.darken(image.getPixel(x, y), theme, brightness));
            }
        }
    }

    /** The color for a fill, or the same color when it should stay. */
    public static int fill(int x0, int y0, int x1, int y1, int argb) {
        if (!DarkModeMod.isModEnabled() || !DarkModeMod.drawnBackgrounds.get() || MycelScreens.current() == null) {
            return argb;
        }
        return Palette.darkenFill(argb, Math.abs(x1 - x0), Math.abs(y1 - y0), DarkModeMod.theme(),
                DarkModeMod.brightness.get());
    }

    private static boolean textApplies() {
        // A sign keeps its wood texture on its edit screen, so its dark text must stay.
        return DarkModeMod.isModEnabled() && DarkModeMod.readableText.get()
                && !(MycelScreens.current() instanceof AbstractSignEditScreen);
    }

    public static int text(int argb) {
        return textApplies() ? Palette.readableText(argb) : argb;
    }

    /** Text can color its own words, so the color passed to the draw call does not cover everything. */
    public static FormattedCharSequence text(FormattedCharSequence text) {
        if (!textApplies()) {
            return text;
        }
        return sink -> text.accept((index, style, codepoint) -> sink.accept(index, readable(style), codepoint));
    }

    private static Style readable(Style style) {
        TextColor color = style.getColor();
        if (color == null) {
            return style;
        }
        int rgb = color.getValue();
        int light = Palette.readableText(rgb) & 0xFFFFFF;
        return light == rgb ? style : style.withColor(TextColor.fromRgb(light));
    }

    /** Wraps vanilla's loading screen color. */
    public static IntSupplier loadingColor(IntSupplier vanilla) {
        return () -> {
            if (!DarkModeMod.isModEnabled()) {
                return DarkModeMod.enabled == null ? LoadingColor.BLACK.argb : vanilla.getAsInt();
            }
            LoadingColor color = DarkModeMod.loadingColor();
            return color == LoadingColor.RED ? vanilla.getAsInt() : color.argb;
        };
    }

    /**
     * Forge's and NeoForge's first loading window opens before any mod loads, and it only reads
     * vanilla's Monochrome Logo option. So that option follows our setting from the next start on.
     */
    public static void syncLoadingOption(Minecraft minecraft) {
        if (DarkModeMod.enabled == null) {
            return;
        }
        Options options = minecraft.options;
        boolean wantDark = DarkModeMod.isModEnabled() && DarkModeMod.loadingColor() != LoadingColor.RED;
        Path marker = minecraft.gameDirectory.toPath().resolve("config").resolve(OPTION_MARKER);
        boolean isDark = options.darkMojangStudiosBackground().get();
        try {
            if (wantDark && !isDark) {
                Files.createDirectories(marker.getParent());
                Files.writeString(marker, "");
                options.darkMojangStudiosBackground().set(true);
                options.save();
            } else if (!wantDark && isDark && Files.deleteIfExists(marker)) {
                options.darkMojangStudiosBackground().set(false);
                options.save();
            }
        } catch (IOException e) {
            LOGGER.warn("Dark Mode could not change the Monochrome Logo option", e);
        }
    }

    /** Runs every client tick: applies texture settings once the settings screen has closed. */
    public static void tick(Minecraft minecraft) {
        Screen screen = MycelScreens.current();
        if (texturesChanged && !(screen instanceof MycelConfigScreen) && !(screen instanceof DarkModeScreen)) {
            texturesChanged = false;
            minecraft.reloadResourcePacks();
        }
    }
}
