package com.mati.donutvisual.mixin;

import com.mati.donutvisual.DonutVisualClient;
import com.mati.donutvisual.LocalFreeCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void donutvisual$turnFreeCamera(double yaw, double pitch, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()
                && client.player == (Object) this
                && DonutVisualClient.isFreecamEnabled()) {
            LocalFreeCamera camera = DonutVisualClient.getFreeCamera();
            if (camera != null) {
                camera.turn(yaw, pitch);
                ci.cancel();
            }
        }
    }

    @Inject(method = "getViewXRot", at = @At("HEAD"), cancellable = true)
    protected void donutvisual$getViewXRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        // LocalPlayerMixin supplies the freecam view rotation for the player.
    }
}
