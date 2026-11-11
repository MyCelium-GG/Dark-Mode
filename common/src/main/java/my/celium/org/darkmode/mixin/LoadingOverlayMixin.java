package my.celium.org.darkmode.mixin;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.client.Darkness;
import my.celium.org.darkmode.client.LoadingScenes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
abstract class LoadingOverlayMixin {
    @Shadow
    @Final
    @Mutable
    private static IntSupplier BRAND_BACKGROUND;

    @Shadow
    @Final
    private boolean fadeIn;

    @Shadow
    private long fadeInStart;

    @Shadow
    private long fadeOutStart;

    /** Black until Dark Mode has started (so the first frames are not red), vanilla's colour without Mycel. */
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void darkmode$wrapColor(CallbackInfo info) {
        IntSupplier vanilla = BRAND_BACKGROUND;
        BRAND_BACKGROUND = () -> DarkGate.active() ? Darkness.loadingColor(vanilla).getAsInt()
                : DarkGate.mycelMissing() ? vanilla.getAsInt() : 0xFF000000;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void darkmode$syncOption(Minecraft minecraft, ReloadInstance reload, Consumer<Optional<Throwable>> onFinish,
            boolean fadeIn, CallbackInfo info) {
        if (DarkGate.active()) {
            Darkness.syncLoadingOption(minecraft);
        }
    }

    /**
     * The Christmas landscape or your own image, drawn after the background and before the logo
     * (the logo's colour is worked out with {@code ARGB.white} right before it is drawn). It fades
     * exactly like the logo does.
     */
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;white(F)I"))
    private void darkmode$scene(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo info) {
        if (!DarkGate.active()) {
            return;
        }
        long now = Util.getMillis();
        float fadeOut = fadeOutStart > -1L ? (now - fadeOutStart) / 1000.0F : -1.0F;
        float fadeInAnim = fadeInStart > -1L ? (now - fadeInStart) / 500.0F : -1.0F;
        float alpha;
        if (fadeOut >= 1.0F) {
            alpha = 1.0F - Mth.clamp(fadeOut - 1.0F, 0.0F, 1.0F);
        } else if (fadeIn) {
            alpha = Mth.clamp(fadeInAnim, 0.0F, 1.0F);
        } else {
            alpha = 1.0F;
        }
        if (LoadingScenes.draw(graphics, alpha)) {
            graphics.nextStratum();
        }
    }
}
