package my.celium.org.darkmode.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PaletteTest {
    private static int dark(int argb) {
        return Palette.darken(argb, Theme.NEUTRAL, 30);
    }

    @Test
    void containerGreyGetsDark() {
        int result = dark(0xFFC6C6C6);
        assertEquals(0xFF3B3B3B, result);
    }

    @Test
    void bevelStaysTheRightWayRound() {
        assertTrue((dark(0xFFFFFFFF) & 0xFF) > (dark(0xFFC6C6C6) & 0xFF));
        assertTrue((dark(0xFFC6C6C6) & 0xFF) > (dark(0xFF555555) & 0xFF));
    }

    @Test
    void saturatedColorsStay() {
        assertEquals(0xFFFF1313, dark(0xFFFF1313));
        assertEquals(0xFF80FF20, dark(0xFF80FF20));
    }

    @Test
    void alphaAndClearPixelsStay() {
        assertEquals(0x00FFFFFF, dark(0x00FFFFFF));
        assertEquals(0x80, dark(0x80FFFFFF) >>> 24);
    }

    @Test
    void themeTints() {
        int result = Palette.darken(0xFFC6C6C6, Theme.MIDNIGHT, 30);
        assertTrue((result & 0xFF) > ((result >> 16) & 0xFF));
    }

    @Test
    void fillsKeepThinLinesAndSeeThroughColors() {
        assertEquals(0xFFFFFFFF, Palette.darkenFill(0xFFFFFFFF, 1, 40, Theme.NEUTRAL, 30));
        assertEquals(0x80FFFFFF, Palette.darkenFill(0x80FFFFFF, 40, 40, Theme.NEUTRAL, 30));
        assertEquals(0xFF202020, Palette.darkenFill(0xFF202020, 40, 40, Theme.NEUTRAL, 30));
        assertEquals(dark(0xFFEEEEEE), Palette.darkenFill(0xFFEEEEEE, 40, 40, Theme.NEUTRAL, 30));
    }

    @Test
    void darkenedFillIsStable() {
        int once = Palette.darkenFill(0xFFEEEEEE, 40, 40, Theme.NEUTRAL, 30);
        assertEquals(once, Palette.darkenFill(once, 40, 40, Theme.NEUTRAL, 30));
    }

    @Test
    void darkGreyTextBecomesLight() {
        assertEquals(0xFFC6C6C6, Palette.readableText(0xFF404040));
        assertEquals(0xFFE6E6E6, Palette.readableText(0xFF000000));
    }

    @Test
    void lightAndColoredTextStays() {
        assertEquals(0xFFFFFFFF, Palette.readableText(0xFFFFFFFF));
        assertEquals(0xFFFF5555, Palette.readableText(0xFFFF5555));
    }

    @Test
    void pureDarkBlueGetsBrighter() {
        assertTrue((Palette.readableText(0xFF0000AA) & 0xFF00) >= 0x4000);
        assertEquals(0xFF00AA00, Palette.readableText(0xFF00AA00));
    }

    @Test
    void darkColoredTextGetsBrightAndKeepsItsHue() {
        int result = Palette.readableText(0xFF500050);
        int red = (result >> 16) & 0xFF;
        int green = (result >> 8) & 0xFF;
        assertTrue(red >= 0xC0);
        assertEquals(red, result & 0xFF);
        assertTrue(green < red);
    }
}
