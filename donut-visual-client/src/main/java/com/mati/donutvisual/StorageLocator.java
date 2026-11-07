package com.mati.donutvisual;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class StorageLocator {
    public record Marker(BlockPos position, String label, double distanceSquared) {}

    private static final GizmoStyle HIGHLIGHT_STYLE =
            GizmoStyle.strokeAndFill(0xFFFF66FF, 2.0F, 0x443F11A5);

    private static List<Marker> markers = List.of();
    private static int ticksUntilScan = 0;

    private StorageLocator() {}

    public static void tick(Minecraft client) {
        if (!DonutVisualClient.storageEspEnabled
                || !client.hasSingleplayerServer()
                || client.player == null
                || client.level == null) {
            clear();
            return;
        }

        if (--ticksUntilScan <= 0) {
            ticksUntilScan = 10;
            scan(client);
        }

        // Gizmos are frame-limited, so submit each marker on every client tick.
        // Always-on-top makes the purple box visible through blocks in local worlds.
        for (Marker marker : markers) {
            Gizmos.cuboid(marker.position(), 0.025F, HIGHLIGHT_STYLE).setAlwaysOnTop();
        }
    }

    public static void scan(Minecraft client) {
        if (!client.hasSingleplayerServer() || client.player == null || client.level == null) {
            clear();
            return;
        }

        BlockPos origin = client.player.blockPosition();
        List<Marker> found = new ArrayList<>();

        // Scan only already-loaded blocks; this never requests or generates chunks.
        for (int dx = -12; dx <= 12; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -12; dz <= 12; dz++) {
                    if (dx * dx + dy * dy + dz * dz > 12 * 12) {
                        continue;
                    }

                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (!client.level.hasChunkAt(pos)) {
                        continue;
                    }

                    BlockState state = client.level.getBlockState(pos);
                    String label = null;

                    if (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST)) {
                        label = "Chest";
                    } else if (state.is(Blocks.BARREL)) {
                        label = "Barrel";
                    } else if (state.is(Blocks.ENDER_CHEST)) {
                        label = "Ender Chest";
                    } else if (state.getBlock() instanceof ShulkerBoxBlock) {
                        label = "Shulker Box";
                    }

                    if (label != null) {
                        found.add(new Marker(pos.immutable(), label, origin.distSqr(pos)));
                    }
                }
            }
        }

        found.sort(Comparator.comparingDouble(Marker::distanceSquared));
        markers = List.copyOf(found);
    }

    public static List<Marker> getMarkers() {
        return markers;
    }

    public static void clear() {
        markers = List.of();
        ticksUntilScan = 0;
    }
}
