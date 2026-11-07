package com.mati.donutvisual.mixin;

import com.mati.donutvisual.DonutVisualClient;
import com.mati.donutvisual.LocalFreeCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends EntityMixin {
    @Override
    protected void donutvisual$getViewXRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && DonutVisualClient.isFreecamEnabled()) {
            LocalFreeCamera camera = DonutVisualClient.getFreeCamera();
            if (camera != null) {
                cir.setReturnValue(camera.getViewXRot(partialTick));
            }
        }
    }

    @Inject(method = "getViewYRot", at = @At("HEAD"), cancellable = true)
    private void donutvisual$getViewYRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer() && DonutVisualClient.isFreecamEnabled()) {
            LocalFreeCamera camera = DonutVisualClient.getFreeCamera();
            if (camera != null) {
                cir.setReturnValue(camera.getViewYRot(partialTick));
            }
        }
    }
}
