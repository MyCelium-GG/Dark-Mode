package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.client.DarkModeScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Puts the settings one click from Options on every loader, with or without a mod list: a normal
 * button in the vanilla button grid, under Credits & Attribution.
 */
@Mixin(OptionsScreen.class)
abstract class OptionsScreenMixin extends Screen {
    private OptionsScreenMixin(Component title) {
        super(title);
    }

    /** Catches the button grid just before vanilla puts it on the screen and adds ours as its last cell. */
    @ModifyArg(method = "init", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToContents(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement darkmode$addButton(LayoutElement contents) {
        if (!DarkGate.active() || !(contents instanceof GridLayout grid)) {
            return contents;
        }
        int[] cells = {0};
        grid.visitChildren(child -> cells[0]++);
        Button button = Button.builder(Component.translatable("darkmode.settings.title"),
                b -> this.minecraft.setScreenAndShow(new DarkModeScreen(this))).build();
        // Two columns. On a fresh row it spans both and sits in the middle, like the Done button.
        int row = cells[0] / 2;
        int column = cells[0] % 2;
        if (column == 0) {
            grid.addChild(button, row, 0, 1, 2);
        } else {
            grid.addChild(button, row, 1);
        }
        return contents;
    }
}
