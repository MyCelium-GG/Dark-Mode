package my.celium.org.darkmode.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ScreenPathsTest {
    private static final List<String> NONE = List.of();

    @Test
    void vanillaTextures() {
        assertTrue(ScreenPaths.isScreenTexture("minecraft", "textures/gui/container/generic_54.png", true, NONE));
        assertTrue(ScreenPaths.isScreenTexture("minecraft", "textures/gui/menu_background.png", false, NONE));
        assertFalse(ScreenPaths.isScreenTexture("minecraft", "textures/gui/title/mojangstudios.png", true, NONE));
        assertFalse(ScreenPaths.isScreenTexture("minecraft", "textures/block/stone.png", true, NONE));
    }

    @Test
    void modTextures() {
        assertTrue(ScreenPaths.isScreenTexture("create", "textures/gui/schematicannon.png", true, NONE));
        assertTrue(ScreenPaths.isScreenTexture("somemod", "textures/screens/bag.png", true, NONE));
        assertFalse(ScreenPaths.isScreenTexture("create", "textures/gui/schematicannon.png", false, NONE));
        assertFalse(ScreenPaths.isScreenTexture("create", "textures/gui/schematicannon.png", true, List.of("create")));
        assertFalse(ScreenPaths.isScreenTexture("somemod", "textures/gui/logo.png", true, NONE));
        assertFalse(ScreenPaths.isScreenTexture("somemod", "textures/item/gui_wand.png", true, NONE));
    }

    @Test
    void sprites() {
        assertTrue(ScreenPaths.isScreenSprite("minecraft", "widget/button", true, false, NONE));
        assertTrue(ScreenPaths.isScreenSprite("minecraft", "container/slot", true, false, NONE));
        assertFalse(ScreenPaths.isScreenSprite("minecraft", "hud/hotbar", true, false, NONE));
        assertTrue(ScreenPaths.isScreenSprite("minecraft", "hud/hotbar", true, true, NONE));
        assertFalse(ScreenPaths.isScreenSprite("minecraft", "icon/accessibility", true, true, NONE));
        assertFalse(ScreenPaths.isScreenSprite("somemod", "button_icon", true, false, NONE));
    }
}
