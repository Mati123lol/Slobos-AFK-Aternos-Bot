package com.mati.donutvisual;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec2;

import java.util.UUID;

/**
 * Detached camera entity for integrated single-player worlds only.
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
        this.getAbilities().flying = true;
        this.getAbilities().setFlyingSpeed(0.05F);
        this.setNoGravity(true);
        this.setOnGround(false);
        super.tick();
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
