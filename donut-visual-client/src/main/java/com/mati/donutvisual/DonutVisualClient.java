package com.mati.donutvisual;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class DonutVisualClient implements ClientModInitializer {
    public static boolean brightnessBoost = false;
    public static boolean noEntityShadows = false;

    private static KeyMapping openGuiKey;
    private static boolean originalOptionsCaptured = false;
    private static double originalGamma;
    private static boolean originalEntityShadows;

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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                client.gui.setScreen(new DonutVisualScreen());
            }
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

        // Uses the supported in-game brightness range; this does not alter server state.
        client.options.gamma().set(brightnessBoost ? 1.0 : originalGamma);
        client.options.entityShadows().set(noEntityShadows ? false : originalEntityShadows);
    }

    public static void toggleBrightnessBoost() {
        brightnessBoost = !brightnessBoost;
    }

    public static void toggleNoEntityShadows() {
        noEntityShadows = !noEntityShadows;
    }
}
