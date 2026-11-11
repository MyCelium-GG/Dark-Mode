package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.client.MenuSnow;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Snow falling behind every menu's buttons with the Winter theme. */
@Mixin(Screen.class)
abstract class ScreenMixin {
    @Shadow
    public int width;

    @Shadow
    public int height;

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void darkmode$snow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo info) {
        if (DarkGate.active()) {
            MenuSnow.draw(graphics, width, height);
        }
    }
}
