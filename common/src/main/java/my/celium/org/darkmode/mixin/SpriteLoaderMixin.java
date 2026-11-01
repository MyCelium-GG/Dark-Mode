package my.celium.org.darkmode.mixin;

import java.util.List;
import java.util.concurrent.Executor;
import my.celium.org.darkmode.client.Darkness;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteLoader.class)
abstract class SpriteLoaderMixin {
    @Shadow
    @Final
    private Identifier location;

    @Inject(method = "stitch", at = @At("HEAD"))
    private void darkmode$recolor(List<SpriteContents> sprites, int mipLevels, Executor executor,
            CallbackInfoReturnable<SpriteLoader.Preparations> result) {
        if (Darkness.isGuiAtlas(location)) {
            for (SpriteContents sprite : sprites) {
                Darkness.spriteLoaded(sprite.name(), ((SpriteContentsAccessor) sprite).darkmode$image());
            }
        }
    }
}
