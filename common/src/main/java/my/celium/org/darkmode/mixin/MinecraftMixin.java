package my.celium.org.darkmode.mixin;

import my.celium.org.darkmode.DarkGate;
import my.celium.org.darkmode.MissingMycel;
import my.celium.org.darkmode.client.Darkness;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void darkmode$tick(CallbackInfo info) {
        if (DarkGate.active()) {
            Darkness.tick((Minecraft) (Object) this);
        } else if (DarkGate.mycelMissing()) {
            MissingMycel.tick((Minecraft) (Object) this);
        }
    }
}
