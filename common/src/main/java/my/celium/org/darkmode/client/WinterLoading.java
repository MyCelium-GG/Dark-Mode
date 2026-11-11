package my.celium.org.darkmode.client;

import static my.celium.org.darkmode.client.PixelArt.hash;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * The Christmas loading screen, all pixel art: a winter night with northern lights, a moon, snowy
 * mountains, pine woods, a little village by a frozen lake, a decorated tree and a snowman. The
 * Mojang logo sits in the sky and the progress bar on the dark ice, so both stay readable.
 *
 * <p>Everything that stands still is painted once and drawn as one image. Only what moves is drawn
 * each frame: twinkling stars and bulbs, chimney smoke, falling snow, and a sleigh that crosses the
 * sky now and then. Client only.
 */
final class WinterLoading {
    /** The night sky's top colour; the loading screen's plain background uses it too. */
    static final int SKY_TOP = 0xFF070D22;
    private static final int SKY_LOW = 0xFF2A4775;
    private static final int SNOW = 0xFFF4F8FF;
    private static final int SNOW_SHADE = 0xFFC9D6EA;
    private static final int MOUNTAIN = 0xFF26395A;
    private static final int MOUNTAIN_LIGHT = 0xFF34507A;
    private static final int MOUNTAIN_SNOW = 0xFF9FB4D3;
    private static final int HILL = 0xFFA9BCD8;
    private static final int HILL_TOP = 0xFFD6E2F2;
    private static final int PINE_FAR = 0xFF1C3A44;
    private static final int PINE = 0xFF123A2C;
    private static final int PINE_LIGHT = 0xFF1E5640;
    private static final int TRUNK = 0xFF4A3020;
    private static final int ICE_TOP = 0xFF162845;
    private static final int ICE_LOW = 0xFF22395C;
    private static final int[] BULBS = {0xFFFF4D5A, 0xFF4BE08A, 0xFFFFD25A, 0xFF6EC8FF};

    /** Art pixels across the screen: the art is drawn larger on big windows, never finer. */
    private static final float ART_WIDTH = 360f;
    /** How long one sleigh trip and the pause after it take, in milliseconds. */
    private static final long SLEIGH_CYCLE = 26_000L;
    private static final long SLEIGH_TRIP = 9_000L;

    private WinterLoading() {
    }

    /** Fills the whole {@code width} x {@code height} screen (GUI pixels), faded by {@code alpha}. */
    static void draw(GuiGraphicsExtractor gfx, int width, int height, float alpha) {
        int scale = Math.max(1, Math.round(width / ART_WIDTH));
        int w = (width + scale - 1) / scale;
        int h = (height + scale - 1) / scale;
        Layout l = new Layout(w, h);
        PixelArt.draw(gfx, "winter:" + w + "x" + h, w, h, scale, alpha, p -> paint(p, l));
        PixelArt.Paint live = new PixelArt.Screen(gfx, scale, alpha);
        long now = System.currentTimeMillis();
        twinkle(live, l, now);
        sleigh(live, l, now);
        smoke(live, l, now);
        treeLights(live, l, now);
        snowfall(live, l, now);
    }

    /** Where everything goes, in art pixels, worked out from the screen's size. */
    private record Layout(int w, int h) {
        int y(double f) {
            return (int) Math.round(h * f);
        }

        int x(double f) {
            return (int) Math.round(w * f);
        }

        /** Mountains stand on this line; the hills cover their feet. */
        int mountainBase() {
            return y(0.74);
        }

        int hillTop() {
            return y(0.715);
        }

        /** The frozen lake runs between these two lines, under the progress bar. */
        int lakeTop() {
            return y(0.785);
        }

        int lakeBottom() {
            return y(0.885);
        }

        int moonX() {
            return x(0.85);
        }

        int moonY() {
            return y(0.2);
        }

        int treeX() {
            return x(0.085);
        }

        int treeH() {
            return Math.max(28, y(0.3));
        }

        int cabinX(int i) {
            return x(0.24) + i * Math.max(26, x(0.075));
        }

        int cabinBase() {
            return lakeTop() + 1;
        }
    }

    // ================================================================ the still picture

    private static void paint(PixelArt.Paint p, Layout l) {
        int w = l.w();
        int h = l.h();
        p.gradient(0, 0, w, l.lakeTop() + 2, SKY_TOP, SKY_LOW);
        aurora(p, l);
        for (int i = 0; i < starCount(l); i++) {
            int sx = starX(i, l);
            int sy = starY(i, l);
            p.fill(sx, sy, sx + 1, sy + 1, 0xDDFFFFFF);
            if (i % 11 == 0) {
                p.fill(sx - 1, sy, sx, sy + 1, 0x66FFFFFF);
                p.fill(sx + 1, sy, sx + 2, sy + 1, 0x66FFFFFF);
                p.fill(sx, sy - 1, sx + 1, sy, 0x66FFFFFF);
                p.fill(sx, sy + 1, sx + 1, sy + 2, 0x66FFFFFF);
            }
        }
        moon(p, l.moonX(), l.moonY(), Math.max(6, l.y(0.045)));
        mountains(p, l);
        hills(p, l);
        for (int i = 0; i < 3; i++) {
            cabin(p, l.cabinX(i), l.cabinBase() - (i == 1 ? 2 : 0), i);
        }
        lake(p, l);
        ground(p, l);
        christmasTree(p, l.treeX(), l.y(0.95), l.treeH(), true, 0);
        snowman(p, l.x(0.9), l.y(0.95), Math.max(14, l.y(0.13)));
    }

    /** Green and violet curtains across the upper sky. */
    private static void aurora(PixelArt.Paint p, Layout l) {
        int base = l.y(0.17);
        for (int x = 0; x < l.w(); x++) {
            double wave = Math.sin(x / 31.0) * l.h() * 0.04 + Math.sin(x / 11.0) * 2;
            int top = base + (int) Math.round(wave);
            double strength = 0.5 + 0.5 * Math.sin(x / 47.0 + 1);
            int a = (int) Math.round(16 + 40 * strength);
            int tall = Math.max(10, l.y(0.09));
            p.gradient(x, top - tall, x + 1, top, 0x008A6CFF, ((a / 2) << 24) | 0x8A6CFF);
            p.gradient(x, top, x + 1, top + tall + 4, (a << 24) | 0x3CF0A0, 0x003CF0A0);
        }
    }

    private static int starCount(Layout l) {
        return Math.max(20, l.w() * l.h() / 260);
    }

    private static int starX(int i, Layout l) {
        return 1 + hash(i * 7 + 1) % Math.max(1, l.w() - 2);
    }

    private static int starY(int i, Layout l) {
        return 1 + hash(i * 13 + 5) % Math.max(1, l.y(0.6));
    }

    private static void moon(PixelArt.Paint p, int cx, int cy, int r) {
        disc(p, cx, cy, r * 2, 0x10FFF6D8);
        disc(p, cx, cy, r + r / 2, 0x1CFFF6D8);
        disc(p, cx, cy, r, 0xFFF7F1DC);
        p.fill(cx - r / 2, cy - r / 3, cx - r / 6, cy, 0xFFDCD3B8);
        p.fill(cx + r / 6, cy + r / 4, cx + r / 2, cy + r / 2, 0xFFDCD3B8);
        p.fill(cx + r / 3, cy - r / 2 - 1, cx + r / 3 + 2, cy - r / 2 + 1, 0xFFE6DEC4);
    }

    private static void disc(PixelArt.Paint p, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int half = (int) Math.floor(Math.sqrt(r * r - dy * dy + 0.5));
            p.fill(cx - half, cy + dy, cx + half + 1, cy + dy + 1, color);
        }
    }

    /** A row of snowy peaks far away. */
    private static void mountains(PixelArt.Paint p, Layout l) {
        int base = l.mountainBase();
        int peaks = Math.max(4, l.w() / 55);
        for (int i = 0; i < peaks; i++) {
            int cx = (int) ((i + 0.5) * l.w() / peaks) + hash(i * 3 + 11) % 17 - 8;
            int height = l.y(0.13) + hash(i * 7 + 2) % Math.max(1, l.y(0.08));
            int half = (int) (height * (1.3 + (hash(i + 40) % 5) / 10.0));
            for (int dy = 0; dy < height; dy++) {
                int y = base - height + dy;
                int span = (int) Math.round((dy + 1) * half / (double) height);
                int jag = dy < height / 3 ? hash(i * 31 + dy) % 2 : 0;
                p.fill(cx - span, y, cx, y + 1, MOUNTAIN_LIGHT);
                p.fill(cx, y, cx + span + 1, y + 1, MOUNTAIN);
                if (dy < height / 3 + jag) {
                    p.fill(cx - span, y, cx + span / 2 + 1, y + 1, MOUNTAIN_SNOW);
                }
            }
        }
    }

    /** Snowy hills with pine woods, the far shore of the lake. */
    private static void hills(PixelArt.Paint p, Layout l) {
        int top = l.hillTop();
        for (int x = 0; x < l.w(); x++) {
            int y = top + (int) Math.round(Math.sin(x / 29.0) * 3 + Math.sin(x / 11.0 + 1) * 1.5);
            p.fill(x, y, x + 1, l.lakeTop() + 2, HILL);
            p.fill(x, y, x + 1, y + 1, HILL_TOP);
        }
        int villageFrom = l.cabinX(0) - 6;
        int villageTo = l.cabinX(2) + 28;
        for (int i = 0; i < l.w() / 6; i++) {
            int tx = 2 + hash(i * 41 + 2) % Math.max(1, l.w() - 4);
            if (tx > villageFrom && tx < villageTo) {
                continue;
            }
            int hgt = 8 + hash(i * 11 + 9) % 9;
            int y = top + (int) Math.round(Math.sin(tx / 29.0) * 3) + 2 + hash(i * 5) % 4;
            pine(p, tx, y, hgt, i % 3 == 0 ? PINE_FAR : PINE, 0xFFEAF1FB);
        }
    }

    private static void pine(PixelArt.Paint p, int cx, int base, int h, int color, int snow) {
        p.fill(cx, base - 2, cx + 1, base, TRUNK);
        int body = h - 2;
        int tierH = Math.max(2, body / 3 + 1);
        for (int tier = 0; tier < 3; tier++) {
            int tierTop = base - 2 - body + tier * (body / 3);
            for (int r = 0; r < tierH; r++) {
                int half = (r + tier + 1) * 2 / 3;
                p.fill(cx - half, tierTop + r, cx + half + 1, tierTop + r + 1, r == 0 || (r == 1 && half > 0) ? snow : color);
            }
        }
    }

    /** A small log cabin with warm windows and snow on the roof. */
    private static void cabin(PixelArt.Paint p, int x, int base, int i) {
        int w = 20;
        int wallTop = base - 8;
        int wall = i == 1 ? 0xFF6A3A2A : 0xFF5A3A26;
        p.fill(x, wallTop, x + w, base, wall);
        for (int y = wallTop + 1; y < base; y += 2) {
            p.fill(x, y, x + w, y + 1, 0xFF4A2E1E);
        }
        p.fill(x + 13, wallTop - 9, x + 16, wallTop - 2, 0xFF6B6E78);
        p.fill(x + 13, wallTop - 10, x + 16, wallTop - 9, SNOW);
        for (int r = 0; r < 7; r++) {
            int y = wallTop - 7 + r;
            p.fill(x + w / 2 - 2 - r * 2, y, x + w / 2 + 2 + r * 2, y + 1, r < 2 || r % 2 == 0 ? SNOW : SNOW_SHADE);
        }
        p.fill(x + 2, wallTop + 1, x + 7, wallTop + 5, 0x55FFC060);
        p.fill(x + 3, wallTop + 2, x + 6, wallTop + 5, 0xFFFFCF6A);
        p.fill(x + 13, wallTop + 1, x + 18, wallTop + 5, 0x55FFC060);
        p.fill(x + 14, wallTop + 2, x + 17, wallTop + 5, 0xFFFFCF6A);
        p.fill(x + 8, wallTop + 2, x + 11, base, 0xFF3A2416);
        p.fill(x - 1, base, x + w + 1, base + 1, SNOW);
    }

    /** The frozen lake: dark ice, the moon's reflection, a few cracks and glints. */
    private static void lake(PixelArt.Paint p, Layout l) {
        int top = l.lakeTop();
        int bottom = l.lakeBottom();
        p.gradient(0, top, l.w(), bottom, ICE_TOP, ICE_LOW);
        for (int x = 0; x < l.w(); x++) {
            int bank = (int) Math.round(Math.sin(x / 17.0) * 1.2);
            p.fill(x, top + bank - 1, x + 1, top + bank + 1, SNOW_SHADE);
        }
        int mx = l.moonX();
        for (int y = top + 2; y < bottom - 1; y += 2) {
            int half = 2 + hash(y * 3) % 4;
            p.fill(mx - half, y, mx + half, y + 1, 0x40F7F1DC);
        }
        for (int i = 0; i < l.w() / 40; i++) {
            int cx = hash(i * 17 + 3) % Math.max(1, l.w());
            int cy = top + 3 + hash(i * 23) % Math.max(1, bottom - top - 5);
            int len = 4 + hash(i * 5 + 1) % 8;
            for (int k = 0; k < len; k++) {
                p.fill(cx + k, cy + (k * (i % 2 == 0 ? 1 : -1)) / 3, cx + k + 1, cy + (k * (i % 2 == 0 ? 1 : -1)) / 3 + 1,
                        0xFF3A5478);
            }
        }
        for (int i = 0; i < l.w() / 9; i++) {
            int gx = hash(i * 29 + 7) % Math.max(1, l.w());
            int gy = top + 2 + hash(i * 37) % Math.max(1, bottom - top - 3);
            p.fill(gx, gy, gx + 1, gy + 1, 0x88BFE3FF);
        }
    }

    /** Deep snow in front, with drifts and sparkles. */
    private static void ground(PixelArt.Paint p, Layout l) {
        int base = l.lakeBottom();
        for (int x = 0; x < l.w(); x++) {
            double wave = Math.sin(x / 19.0) * 1.8 + Math.sin(x / 6.0) * 0.6;
            int top = base + (int) Math.round(wave);
            p.gradient(x, top, x + 1, l.h(), SNOW, 0xFFD3DEF0);
            p.fill(x, top, x + 1, top + 1, 0xFFFFFFFF);
            int n = hash(x * 3 + 1);
            if (n % 9 == 0) {
                int sy = top + 3 + n % Math.max(1, l.h() - top - 4);
                p.fill(x, sy, x + 1, sy + 1, SNOW_SHADE);
            }
            if (n % 29 == 0) {
                int sy = top + 2 + (n >> 4) % Math.max(1, l.h() - top - 3);
                p.fill(x, sy, x + 1, sy + 1, 0xFFBFE3FF);
            }
        }
    }

    /**
     * A Christmas tree with snow on its tiers, bulbs and a gold star. With {@code body} it paints
     * the tree; without, only the bulbs and star, whose colours change over time ({@code tick}).
     */
    private static void christmasTree(PixelArt.Paint p, int cx, int base, int h, boolean body, long tick) {
        int rows = h - 6;
        int tier = Math.max(3, rows / 4);
        if (body) {
            p.fill(cx - 1, base - 4, cx + 2, base, TRUNK);
        }
        int bulb = 0;
        for (int r = 0; r < rows; r++) {
            int y = base - 4 - rows + r;
            int tierRow = r % tier;
            int half = 1 + r / 3 + tierRow / 2;
            if (body) {
                p.fill(cx - half, y, cx + half + 1, y + 1, r % 2 == 0 ? PINE : PINE_LIGHT);
                if (tierRow == 0 && r > 0) {
                    p.fill(cx - half, y, cx - half + 2, y + 1, 0xFFEAF1FB);
                    p.fill(cx + half - 1, y, cx + half + 1, y + 1, 0xFFEAF1FB);
                }
            } else if (r > 3 && r % 4 == 2) {
                for (int bx = cx - half + 2; bx < cx + half - 1; bx += 4) {
                    int color = BULBS[(bulb + (int) (tick % BULBS.length)) % BULBS.length];
                    int px = bx + (r / 4) % 2;
                    p.fill(px, y, px + 1, y + 1, color);
                    bulb++;
                }
            }
        }
        if (!body) {
            int sy = base - 4 - rows - 3;
            int star = tick % 6 == 0 ? 0xFFFFF2B0 : 0xFFFFD25A;
            p.fill(cx, sy - 1, cx + 1, sy + 4, star);
            p.fill(cx - 2, sy + 1, cx + 3, sy + 2, star);
            p.fill(cx - 1, sy, cx + 2, sy + 3, star);
        }
    }

    /** A snowman with a red scarf, coal buttons and a carrot nose; {@code h} tall, standing at {@code base}. */
    private static void snowman(PixelArt.Paint p, int cx, int base, int h) {
        int r1 = Math.max(4, h * 2 / 9);
        int r2 = Math.max(3, h / 6);
        int r3 = Math.max(2, h / 8);
        int c1 = base - r1;
        int c2 = c1 - r1 - r2 + 2;
        int c3 = c2 - r2 - r3 + 1;
        // A blue-grey outline first, so the white snowman stands out against the white snow.
        for (int[] ball : new int[][] {{c1, r1}, {c2, r2}, {c3, r3}}) {
            disc(p, cx, ball[0], ball[1] + 1, 0xFF8FA3C2);
        }
        for (int[] ball : new int[][] {{c1, r1}, {c2, r2}, {c3, r3}}) {
            disc(p, cx, ball[0], ball[1], SNOW);
            for (int dy = -ball[1]; dy <= ball[1]; dy++) {
                int half = (int) Math.floor(Math.sqrt(ball[1] * ball[1] - dy * dy + 0.5));
                p.fill(cx + half - 1, ball[0] + dy, cx + half + 1, ball[0] + dy + 1, SNOW_SHADE);
            }
        }
        p.fill(cx - r2, c2 - r2 + 1, cx + r2 + 1, c2 - r2 + 3, 0xFFC92A37);
        p.fill(cx + r2 - 2, c2 - r2 + 3, cx + r2, c2 - r2 + 7, 0xFFC92A37);
        p.fill(cx - 2, c3 - 1, cx - 1, c3, 0xFF1A1A1A);
        p.fill(cx + 1, c3 - 1, cx + 2, c3, 0xFF1A1A1A);
        p.fill(cx, c3 + 1, cx - 3, c3 + 2, 0xFFF08A24);
        p.fill(cx - 3, c3 + 1, cx, c3 + 2, 0xFFF08A24);
        for (int i = 0; i < 3; i++) {
            p.fill(cx, c2 - r2 / 2 + i * 2 + 2, cx + 1, c2 - r2 / 2 + i * 2 + 3, 0xFF1A1A1A);
        }
        p.fill(cx - r3 - 1, c3 - r3, cx + r3 + 2, c3 - r3 + 1, 0xFF1A1A1A);
        p.fill(cx - r3 + 1, c3 - r3 - 4, cx + r3, c3 - r3, 0xFF1A1A1A);
        p.fill(cx - r3 + 1, c3 - r3 - 1, cx + r3, c3 - r3, 0xFFC92A37);
        for (int k = 0; k < r2 + 3; k++) {
            p.fill(cx - r2 - k, c2 - k / 2, cx - r2 - k + 1, c2 - k / 2 + 1, TRUNK);
            p.fill(cx + r2 + k, c2 - k / 2, cx + r2 + k + 1, c2 - k / 2 + 1, TRUNK);
        }
    }

    // ================================================================ what moves

    private static void twinkle(PixelArt.Paint p, Layout l, long now) {
        long t = now / 600;
        for (int i = 0; i < starCount(l); i++) {
            if ((t + i) % 9 != 0) {
                continue;
            }
            int sx = starX(i, l);
            int sy = starY(i, l);
            float f = Math.clamp(sy / (float) Math.max(1, l.lakeTop() + 2), 0f, 1f);
            p.fill(sx, sy, sx + 1, sy + 1, PixelArt.lerp(SKY_TOP, SKY_LOW, f) & 0xFFFFFF | 0xBB000000);
        }
    }

    /** Smoke from the three chimneys, and windows that flicker now and then. */
    private static void smoke(PixelArt.Paint p, Layout l, long now) {
        double t = now / 1000.0;
        for (int c = 0; c < 3; c++) {
            int x = l.cabinX(c);
            int wallTop = l.cabinBase() - (c == 1 ? 2 : 0) - 8;
            if ((now / 900 + c * 2) % 7 == 0) {
                p.fill(x + 3, wallTop + 2, x + 6, wallTop + 5, 0xFFFFB347);
            }
            for (int i = 0; i < 4; i++) {
                double life = (t * 0.45 + i / 4.0 + c * 0.3) % 1.0;
                int px = x + 14 + (int) Math.round(Math.sin(t + i + c) * 2 + life * 6);
                int py = wallTop - 12 - (int) Math.round(life * 16);
                p.fill(px, py, px + 2, py + 2, PixelArt.fade(0xFFCFD8E6, 0.55f * (1f - (float) life)));
            }
        }
    }

    private static void treeLights(PixelArt.Paint p, Layout l, long now) {
        christmasTree(p, l.treeX(), l.y(0.95), l.treeH(), false, now / 350);
    }

    /** A sleigh with two reindeer crossing the sky every so often, passing in front of the moon. */
    private static void sleigh(PixelArt.Paint p, Layout l, long now) {
        long t = now % SLEIGH_CYCLE;
        if (t > SLEIGH_TRIP) {
            return;
        }
        double f = t / (double) SLEIGH_TRIP;
        int x = (int) Math.round(-30 + f * (l.w() + 60));
        int y = l.moonY() - 2 + (int) Math.round(Math.sin(f * Math.PI * 2) * 3 - f * l.h() * 0.06);
        int dark = 0xFF0B1226;
        // Sleigh: runner, body with a curled front, a sack of gifts.
        p.fill(x - 12, y + 3, x, y + 4, dark);
        p.fill(x - 13, y + 2, x - 12, y + 3, dark);
        p.fill(x - 11, y, x - 1, y + 3, 0xFF8A1A22);
        p.fill(x - 1, y - 1, x, y + 3, 0xFF8A1A22);
        p.fill(x - 10, y - 3, x - 6, y, 0xFF5A3A26);
        // Reins and two reindeer.
        p.fill(x, y + 1, x + 6, y + 2, dark);
        for (int d = 0; d < 2; d++) {
            int rx = x + 6 + d * 9;
            int ry = y - 1 + ((now / 160 + d) % 2 == 0 ? 0 : 1);
            p.fill(rx, ry, rx + 5, ry + 2, dark);
            p.fill(rx + 4, ry - 2, rx + 6, ry, dark);
            p.fill(rx + 4, ry - 4, rx + 5, ry - 2, dark);
            p.fill(rx + 6, ry - 4, rx + 7, ry - 3, dark);
            p.fill(rx, ry + 2, rx + 1, ry + 4, dark);
            p.fill(rx + 4, ry + 2, rx + 5, ry + 4, dark);
            if (d == 1 && (now / 400) % 2 == 0) {
                p.fill(rx + 6, ry - 1, rx + 7, ry, 0xFFFF3B3B);
            }
        }
    }

    /** Flakes drifting down over everything. */
    private static void snowfall(PixelArt.Paint p, Layout l, long now) {
        double t = now / 1000.0;
        int count = Math.max(40, l.w() * l.h() / 700);
        for (int i = 0; i < count; i++) {
            int n = hash(i * 31 + 7);
            double speed = 5 + n % 10;
            double y = (n % l.h() + t * speed) % l.h();
            double sway = Math.sin(t * (0.5 + (n % 5) * 0.15) + i) * 3;
            int x = (int) Math.floorMod((long) (hash(i * 17 + 3) % l.w() + sway), (long) l.w());
            int size = n % 4 == 0 ? 2 : 1;
            p.fill(x, (int) y, x + size, (int) y + size, PixelArt.fade(0xFFFFFFFF, 0.45f + (n % 4) * 0.13f));
        }
    }
}
