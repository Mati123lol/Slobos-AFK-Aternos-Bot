package com.mati.donutvisual;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class StorageHud {
    private StorageHud() {}

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("donutvisual", "storage_esp"),
                StorageHud::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (!DonutVisualClient.storageEspEnabled
                || !client.hasSingleplayerServer()
                || client.player == null) {
            return;
        }

        List<StorageLocator.Marker> markers = StorageLocator.getMarkers();
        int visible = Math.min(markers.size(), 8);
        int panelHeight = 22 + visible * 13;

        graphics.fill(8, 8, 218, 30 + visible * 13, 0xC9100D20);
        graphics.fill(8, 8, 218, 10, 0xFFB65CFF);
        graphics.outline(8, 8, 210, panelHeight, 0xFF6D3C91);
        graphics.text(client.font, "STORAGE ESP  |  200 BLOCKS", 16, 15, 0xFFE8CFFF, true);

        int y = 34;
        for (int i = 0; i < visible; i++) {
            StorageLocator.Marker marker = markers.get(i);
            BlockPosView pos = new BlockPosView(marker.position().getX(), marker.position().getY(), marker.position().getZ());
            int distance = (int) Math.round(Math.sqrt(marker.distanceSquared()));
            String row = marker.label() + "  " + pos.x() + " " + pos.y() + " " + pos.z() + "  (" + distance + "m)";
            graphics.text(client.font, row, 16, y, 0xFFFFFFFF, true);
            y += 13;
        }

        if (StorageLocator.isScanning()) {
            graphics.text(client.font, "Scanning loaded chunks...", 16, 35, 0xFFB9AFCB, true);
        } else if (markers.isEmpty()) {
            graphics.text(client.font, "No storage blocks found in loaded area.", 16, 35, 0xFFB9AFCB, true);
        } else if (markers.size() > visible) {
            graphics.text(client.font, "+" + (markers.size() - visible) + " more nearby", 16, y, 0xFFB9AFCB, true);
        }
    }

    private record BlockPosView(int x, int y, int z) {}
}
