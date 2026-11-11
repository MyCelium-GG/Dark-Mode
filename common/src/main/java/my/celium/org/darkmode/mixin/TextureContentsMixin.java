package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.client.Darkness;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureContents.class)
abstract class TextureContentsMixin {
    @Inject(method = "load", at = @At("RETURN"))
    private static void darkmode$recolor(ResourceManager manager, Identifier location,
            CallbackInfoReturnable<TextureContents> result) {
        if (DarkGate.active()) {
            Darkness.textureLoaded(location, result.getReturnValue().image());
        }
    }
}
