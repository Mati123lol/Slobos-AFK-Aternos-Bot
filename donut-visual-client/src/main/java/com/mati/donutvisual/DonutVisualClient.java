package com.mati.donutvisual;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class DonutVisualClient implements ClientModInitializer {
    public static boolean brightnessBoost = false;
    public static boolean noEntityShadows = false;
    public static boolean storageEspEnabled = false;

    private static KeyMapping openGuiKey;
    private static KeyMapping toggleStorageKey;
    private static KeyMapping toggleFreecamKey;
    private static boolean originalOptionsCaptured = false;
    private static double originalGamma;
    private static boolean originalEntityShadows;

    private static LocalFreeCamera freeCamera;
    private static ClientInput previousPlayerInput;
    private static boolean previousSmartCull;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("donutvisual", "main")
        );

        openGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.donutvisual.open_gui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));
        toggleStorageKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.donutvisual.toggle_storage",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                category
        ));
        toggleFreecamKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.donutvisual.toggle_freecam",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                category
        ));

        StorageHud.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                client.gui.setScreen(new DonutVisualScreen());
            }
            while (toggleStorageKey.consumeClick()) {
                toggleStorageEsp(client);
            }
            while (toggleFreecamKey.consumeClick()) {
                toggleFreecam(client);
            }

            if (freeCamera != null && (client.player == null || client.level == null)) {
                disableFreecam(client);
            }

            StorageLocator.tick(client);
            applyLocalVisualOptions(client);
        });
    }

    private static void applyLocalVisualOptions(Minecraft client) {
        if (client.options == null) {
            return;
        }

        if (!originalOptionsCaptured) {
            originalGamma = client.options.gamma().get();
            originalEntityShadows = client.options.entityShadows().get();
            originalOptionsCaptured = true;
        }

        client.options.gamma().set(brightnessBoost ? 1.0 : originalGamma);
        client.options.entityShadows().set(noEntityShadows ? false : originalEntityShadows);
    }

    public static void toggleBrightnessBoost() {
        brightnessBoost = !brightnessBoost;
    }

    public static void toggleNoEntityShadows() {
        noEntityShadows = !noEntityShadows;
    }

    public static void toggleStorageEsp(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }
        storageEspEnabled = !storageEspEnabled;
        if (!storageEspEnabled) {
            StorageLocator.clear();
        } else {
            StorageLocator.scan(client);
        }
    }

    public static void toggleFreecam(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }

        if (freeCamera == null) {
            previousPlayerInput = client.player.input;
            previousSmartCull = client.smartCull;
            client.smartCull = false;
            client.player.input = new ClientInput();

            freeCamera = new LocalFreeCamera(client);
            freeCamera.snapTo(
                    client.player.getX(),
                    client.player.getY(),
                    client.player.getZ(),
                    client.player.getYRot(),
                    client.player.getXRot()
            );
            freeCamera.spawn();
            client.setCameraEntity(freeCamera);
        } else {
            disableFreecam(client);
        }
    }

    private static void disableFreecam(Minecraft client) {
        if (freeCamera == null) {
            return;
        }

        if (client.player != null) {
            client.setCameraEntity(client.player);
            if (previousPlayerInput != null) {
                client.player.input = previousPlayerInput;
            }
        }

        client.smartCull = previousSmartCull;
        freeCamera.despawn();
        freeCamera = null;
        previousPlayerInput = null;
    }

    public static boolean isFreecamEnabled() {
        return freeCamera != null;
    }

    public static LocalFreeCamera getFreeCamera() {
        return freeCamera;
    }

    public static boolean privateToolsAvailable(Minecraft client) {
        return client.player != null && client.level != null;
    }
}
