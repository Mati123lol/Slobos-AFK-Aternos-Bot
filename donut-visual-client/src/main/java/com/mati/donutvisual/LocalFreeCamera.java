package com.mati.donutvisual;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Detached camera entity used only in integrated single-player worlds.
 */
public final class LocalFreeCamera extends AbstractClientPlayer {
    private final KeyboardInput cameraInput;
    private final Minecraft client;

    public LocalFreeCamera(Minecraft client) {
        super((ClientLevel) client.level, new GameProfile(UUID.randomUUID(), "DonutCamera"));
        this.client = client;
        this.cameraInput = new KeyboardInput(client.options);
        this.setId(-420);
        this.setPose(Pose.SWIMMING);
        this.getAbilities().flying = true;
        this.getAbilities().setFlyingSpeed(0.0F);
        this.setNoGravity(true);
    }

    public void spawn() {
        ((ClientLevel) this.level()).addEntity(this);
    }

    public void despawn() {
        ((ClientLevel) this.level()).removeEntity(this.getId(), Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void tick() {
        cameraInput.tick();
        applyFreeFlightVelocity();

        this.getAbilities().flying = true;
        this.getAbilities().setFlyingSpeed(0.0F);
        this.setNoGravity(true);
        this.setOnGround(false);

        super.tick();
    }

    /**
     * Apply velocity directly to the detached camera. The old implementation
     * only populated movement impulses, which did not reliably move this
     * client-side player entity on Minecraft 26.2.
     */
    private void applyFreeFlightVelocity() {
        float yaw = this.getYRot();
        Vec3 forward = Vec3.directionFromRotation(0.0F, yaw);
        Vec3 right = Vec3.directionFromRotation(0.0F, yaw + 90.0F);

        double x = 0.0;
        double z = 0.0;

        if (cameraInput.keyPresses.forward()) {
            x += forward.x;
            z += forward.z;
        }
        if (cameraInput.keyPresses.backward()) {
            x -= forward.x;
            z -= forward.z;
        }
        if (cameraInput.keyPresses.right()) {
            x += right.x;
            z += right.z;
        }
        if (cameraInput.keyPresses.left()) {
            x -= right.x;
            z -= right.z;
        }

        double length = Math.sqrt(x * x + z * z);
        double speed = client.options.keySprint.isDown() ? 0.48 : 0.22;
        if (length > 0.0) {
            x = (x / Math.max(1.0, length)) * speed;
            z = (z / Math.max(1.0, length)) * speed;
        }

        double y = 0.0;
        if (cameraInput.keyPresses.jump()) {
            y += speed;
        }
        if (cameraInput.keyPresses.shift()) {
            y -= speed;
        }

        this.setDeltaMovement(x, y, z);
    }

    @Override
    protected void applyInput() {
        Vec2 move = cameraInput.getMoveVector();
        if (move.lengthSquared() != 0.0F) {
            move = move.scale(0.98F);
        }

        this.xxa = move.x;
        this.zza = move.y;
        this.jumping = cameraInput.keyPresses.jump();
        this.setSprinting(client.options.keySprint.isDown() && cameraInput.keyPresses.forward());
    }

    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    @Override
    public float getViewXRot(float partialTick) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float partialTick) {
        return this.getYRot();
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }
}
