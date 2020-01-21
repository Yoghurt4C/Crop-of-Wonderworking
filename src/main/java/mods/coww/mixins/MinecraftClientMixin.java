package mods.coww.mixins;

import mods.coww.client.CropWonderWorkingClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cowwInit(CallbackInfo ctx) {
        CropWonderWorkingClient.get().onInit((MinecraftClient) (Object) this);
    }
}