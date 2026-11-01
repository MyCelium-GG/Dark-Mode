package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.client.Darkness;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
abstract class GuiGraphicsExtractorMixin {
    @Shadow
    public abstract void fill(int x0, int y0, int x1, int y1, int color);

    @Shadow
    public abstract void fillGradient(int x0, int y0, int x1, int y1, int color0, int color1);

    /** A darkened color is dark, so the second pass through here changes nothing and ends. */
    @Inject(method = "fill(IIIII)V", at = @At("HEAD"), cancellable = true)
    private void darkmode$fill(int x0, int y0, int x1, int y1, int color, CallbackInfo info) {
        int dark = Darkness.fill(x0, y0, x1, y1, color);
        if (dark != color) {
            fill(x0, y0, x1, y1, dark);
            info.cancel();
        }
    }

    @Inject(method = "fillGradient(IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void darkmode$gradient(int x0, int y0, int x1, int y1, int color0, int color1, CallbackInfo info) {
        int dark0 = Darkness.fill(x0, y0, x1, y1, color0);
        int dark1 = Darkness.fill(x0, y0, x1, y1, color1);
        if (dark0 != color0 || dark1 != color1) {
            fillGradient(x0, y0, x1, y1, dark0, dark1);
            info.cancel();
        }
    }
}
