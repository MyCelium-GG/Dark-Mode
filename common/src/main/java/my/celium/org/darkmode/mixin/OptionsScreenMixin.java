package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.client.DarkModeScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Puts the settings one click from Options on every loader, with or without a mod list. */
@Mixin(OptionsScreen.class)
abstract class OptionsScreenMixin extends Screen {
    private OptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void darkmode$addButton(CallbackInfo info) {
        if (!DarkGate.active()) {
            return;
        }
        addRenderableWidget(Button.builder(Component.translatable("darkmode.settings.title"),
                button -> this.minecraft.setScreenAndShow(new DarkModeScreen(this))).bounds(6, 6, 80, 20).build());
    }
}
