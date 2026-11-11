package my.celium.org.darkmode.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

/**
 * Your own loading screen: a PNG at {@code config/darkmode/loading.png}, put there by dragging an
 * image onto the Dark Mode settings screen or by copying it in. It fills the window behind the
 * Mojang logo, cut to fit like a wallpaper, and slightly dimmed so the logo stays readable.
 */
public final class CustomLoading {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier ID = Identifier.fromNamespaceAndPath("darkmode", "custom_loading");
    private static final int DIM = 0x48000000;

    private static long loadedStamp = Long.MIN_VALUE;
    private static int texW;
    private static int texH;
    private static boolean ready;

    private CustomLoading() {
    }

    /** {@code config/darkmode/loading.png}. */
    public static Path file() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("darkmode").resolve("loading.png");
    }

    public static boolean exists() {
        return Files.isRegularFile(file());
    }

    /**
     * Copies a PNG the player dropped in as the loading image. Returns false (and changes nothing)
     * when the file is not a readable PNG.
     */
    public static boolean importImage(Path source) {
        if (!source.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")) {
            return false;
        }
        try (InputStream in = Files.newInputStream(source)) {
            NativeImage.read(in).close();
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Dark Mode could not read {} as a PNG", source, e);
            return false;
        }
        try {
            Files.createDirectories(file().getParent());
            Files.copy(source, file(), StandardCopyOption.REPLACE_EXISTING);
            loadedStamp = Long.MIN_VALUE;
            return true;
        } catch (IOException e) {
            LOGGER.warn("Dark Mode could not copy {} to {}", source, file(), e);
            return false;
        }
    }

    /** Draws the image over the whole screen; returns false when there is no usable image. */
    static boolean draw(GuiGraphicsExtractor gfx, int width, int height, float alpha) {
        if (!load()) {
            return false;
        }
        // Cover the screen: scale the image so it fills both ways, then centre and cut what sticks out.
        double screen = width / (double) height;
        double image = texW / (double) texH;
        int regionW = texW;
        int regionH = texH;
        if (image > screen) {
            regionW = (int) Math.round(texH * screen);
        } else {
            regionH = (int) Math.round(texW / screen);
        }
        float u = (texW - regionW) / 2f;
        float v = (texH - regionH) / 2f;
        PixelArt.image(gfx, ID, 0, 0, width, height, u, v, regionW, regionH, texW, texH, alpha);
        gfx.fill(0, 0, width, height, PixelArt.fade(DIM, alpha));
        return true;
    }

    /** Loads or reloads the texture when the file changed; a missing or broken file means no image. */
    private static boolean load() {
        Path path = file();
        long stamp;
        try {
            stamp = Files.isRegularFile(path) ? Files.getLastModifiedTime(path).toMillis() ^ Files.size(path) : -1L;
        } catch (IOException e) {
            stamp = -1L;
        }
        if (stamp == loadedStamp) {
            return ready;
        }
        loadedStamp = stamp;
        ready = false;
        if (stamp == -1L) {
            Minecraft.getInstance().getTextureManager().release(ID);
            return false;
        }
        try (InputStream in = Files.newInputStream(path)) {
            NativeImage image = NativeImage.read(in);
            texW = image.getWidth();
            texH = image.getHeight();
            Minecraft.getInstance().getTextureManager().register(ID,
                    new DynamicTexture(() -> "Dark Mode custom loading screen", image));
            ready = texW > 0 && texH > 0;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Dark Mode could not load {}", path, e);
        }
        return ready;
    }
}
