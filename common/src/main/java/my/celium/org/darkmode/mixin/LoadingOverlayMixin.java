package my.celium.org.darkmode.mixin;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import my.celium.org.darkmode.client.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
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

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void darkmode$wrapColor(CallbackInfo info) {
        BRAND_BACKGROUND = Darkness.loadingColor(BRAND_BACKGROUND);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void darkmode$syncOption(Minecraft minecraft, ReloadInstance reload, Consumer<Optional<Throwable>> onFinish,
            boolean fadeIn, CallbackInfo info) {
        Darkness.syncLoadingOption(minecraft);
    }
}
