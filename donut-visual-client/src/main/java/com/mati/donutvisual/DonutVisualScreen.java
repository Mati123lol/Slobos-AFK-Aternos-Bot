package com.mati.donutvisual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class DonutVisualScreen extends Screen {
    private static final int PURPLE = 0xFFB65CFF;
    private static final int PANEL = 0xEE100D20;
    private static final int MUTED = 0xFFB9AFCB;

    public DonutVisualScreen() {
        super(Component.literal("Donut Visual Client"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 66;
        Minecraft client = Minecraft.getInstance();

        this.addRenderableWidget(Button.builder(brightnessLabel(), button -> {
            DonutVisualClient.toggleBrightnessBoost();
            button.setMessage(brightnessLabel());
        }).bounds(centerX - 112, top, 224, 20).build());

        this.addRenderableWidget(Button.builder(shadowsLabel(), button -> {
            DonutVisualClient.toggleNoEntityShadows();
            button.setMessage(shadowsLabel());
        }).bounds(centerX - 112, top + 25, 224, 20).build());

        this.addRenderableWidget(Button.builder(storageLabel(client), button -> {
            DonutVisualClient.toggleStorageEsp(client);
            button.setMessage(storageLabel(client));
        }).bounds(centerX - 112, top + 50, 224, 20).build());

        this.addRenderableWidget(Button.builder(freecamLabel(client), button -> {
            DonutVisualClient.toggleFreecam(client);
            button.setMessage(freecamLabel(client));
        }).bounds(centerX - 112, top + 75, 224, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
                .bounds(centerX - 60, top + 108, 120, 20)
                .build());
    }

    private static Component brightnessLabel() {
        return Component.literal("Brightness Boost: " + (DonutVisualClient.brightnessBoost ? "ON" : "OFF"));
    }

    private static Component shadowsLabel() {
        return Component.literal("No Entity Shadows: " + (DonutVisualClient.noEntityShadows ? "ON" : "OFF"));
    }

    private static Component storageLabel(Minecraft client) {
        if (!DonutVisualClient.privateToolsAvailable(client)) {
            return Component.literal("Storage ESP: Join a world first");
        }
        return Component.literal("Storage ESP: " + (DonutVisualClient.storageEspEnabled ? "ON" : "OFF"));
    }

    private static Component freecamLabel(Minecraft client) {
        if (!DonutVisualClient.privateToolsAvailable(client)) {
            return Component.literal("Freecam: Join a world first");
        }
        return Component.literal("Freecam: " + (DonutVisualClient.isFreecamEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int left = this.width / 2 - 140;
        int top = this.height / 2 - 116;
        int right = this.width / 2 + 140;
        int bottom = this.height / 2 + 143;

        graphics.fill(left, top, right, bottom, PANEL);
        graphics.fill(left, top, right, top + 3, PURPLE);
        graphics.outline(left, top, right - left, bottom - top, 0xFF51306F);

        graphics.text(this.font, "DONUT VISUAL CLIENT", this.width / 2 - 75, top + 12, 0xFFFFFFFF, true);
        graphics.text(this.font, "26.2  |  FABRIC  |  PURPLE EDITION", this.width / 2 - 99, top + 29, PURPLE, true);
        graphics.text(this.font, "Multiplayer supported where permitted | loaded chunks only", this.width / 2 - 128, bottom - 20, MUTED, true);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(null);
    }
}
