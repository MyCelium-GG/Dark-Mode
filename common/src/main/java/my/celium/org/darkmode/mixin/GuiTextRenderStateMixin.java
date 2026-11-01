package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.client.Darkness;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Every piece of GUI text ends up here, whichever draw call it took; books skip the plain text calls. */
@Mixin(GuiTextRenderState.class)
abstract class GuiTextRenderStateMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static int darkmode$color(int color) {
        return Darkness.text(color);
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static FormattedCharSequence darkmode$styles(FormattedCharSequence text) {
        return Darkness.text(text);
    }
}
