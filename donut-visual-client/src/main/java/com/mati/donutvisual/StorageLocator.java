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

/**
 * Searches an approximately 200-block horizontal radius, with a 32-block
 * vertical band, but only within chunks already loaded by the client.
 * The scan is spread over multiple ticks to avoid a large frame hitch.
 */
public final class StorageLocator {
    public static final int RANGE = 200;
    private static final int VERTICAL_RANGE = 32;
    private static final int SCAN_COLUMNS_PER_TICK = 5;
    private static final int RESCAN_DELAY_TICKS = 40;

    public record Marker(BlockPos position, String label, double distanceSquared) {}

    private static final GizmoStyle HIGHLIGHT_STYLE =
            GizmoStyle.strokeAndFill(0xFFFF66FF, 2.0F, 0x443F11A5);

    private static List<Marker> markers = List.of();
    private static List<Marker> workingMarkers;
    private static boolean[][] loadedChunks;
    private static int minChunkX;
    private static int minChunkZ;
    private static int scanOriginX;
    private static int scanOriginY;
    private static int scanOriginZ;
    private static int nextDx;
    private static int ticksUntilRescan;

    private StorageLocator() {}

    public static void tick(Minecraft client) {
        if (!DonutVisualClient.storageEspEnabled
                || !client.hasSingleplayerServer()
                || client.player == null
                || client.level == null) {
            clear();
            return;
        }

        if (workingMarkers == null) {
            if (ticksUntilRescan-- <= 0) {
                beginScan(client);
            }
        } else if (client.player.blockPosition().distSqr(
                new BlockPos(scanOriginX, scanOriginY, scanOriginZ)) > 64.0) {
            // The player moved substantially while the previous scan was running.
            beginScan(client);
        }

        if (workingMarkers != null) {
            scanBatch(client);
        }

        // Re-submit each frame-limited gizmo to keep all highlights visible.
        for (Marker marker : markers) {
            Gizmos.cuboid(marker.position(), 0.025F, HIGHLIGHT_STYLE).setAlwaysOnTop();
        }
    }

    public static void scan(Minecraft client) {
        if (!client.hasSingleplayerServer() || client.player == null || client.level == null) {
            clear();
            return;
        }
        beginScan(client);
        scanBatch(client);
    }

    private static void beginScan(Minecraft client) {
        if (client.player == null || client.level == null) {
            clear();
            return;
        }

        BlockPos origin = client.player.blockPosition();
        scanOriginX = origin.getX();
        scanOriginY = origin.getY();
        scanOriginZ = origin.getZ();

        int maxChunkX = (scanOriginX + RANGE) >> 4;
        int maxChunkZ = (scanOriginZ + RANGE) >> 4;
        minChunkX = (scanOriginX - RANGE) >> 4;
        minChunkZ = (scanOriginZ - RANGE) >> 4;
        loadedChunks = new boolean[maxChunkX - minChunkX + 1][maxChunkZ - minChunkZ + 1];

        // Check chunk availability once per chunk. Never load or request chunks.
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                BlockPos probe = new BlockPos((chunkX << 4) + 8, scanOriginY, (chunkZ << 4) + 8);
                loadedChunks[chunkX - minChunkX][chunkZ - minChunkZ] = client.level.hasChunkAt(probe);
            }
        }

        workingMarkers = new ArrayList<>();
        nextDx = -RANGE;
        ticksUntilRescan = RESCAN_DELAY_TICKS;
    }

    private static void scanBatch(Minecraft client) {
        if (workingMarkers == null || client.level == null) {
            return;
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int minY = client.level.getMinY();
        int maxY = client.level.getMaxY();

        for (int column = 0;
             column < SCAN_COLUMNS_PER_TICK && nextDx <= RANGE;
             column++, nextDx++) {

            int dx = nextDx;
            int worldX = scanOriginX + dx;
            int chunkX = worldX >> 4;
            int chunkIndexX = chunkX - minChunkX;

            for (int dz = -RANGE; dz <= RANGE; dz++) {
                if (dx * dx + dz * dz > RANGE * RANGE) {
                    continue;
                }

                int worldZ = scanOriginZ + dz;
                int chunkZ = worldZ >> 4;
                int chunkIndexZ = chunkZ - minChunkZ;

                if (chunkIndexX < 0 || chunkIndexX >= loadedChunks.length
                        || chunkIndexZ < 0 || chunkIndexZ >= loadedChunks[0].length
                        || !loadedChunks[chunkIndexX][chunkIndexZ]) {
                    continue;
                }

                for (int dy = -VERTICAL_RANGE; dy <= VERTICAL_RANGE; dy++) {
                    int y = scanOriginY + dy;
                    if (y < minY || y >= maxY) {
                        continue;
                    }

                    cursor.set(worldX, y, worldZ);
                    BlockState state = client.level.getBlockState(cursor);
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
                        BlockPos immutablePos = cursor.immutable();
                        workingMarkers.add(new Marker(
                                immutablePos,
                                label,
                                originDistanceSquared(immutablePos)
                        ));
                    }
                }
            }
        }

        if (nextDx > RANGE) {
            workingMarkers.sort(Comparator.comparingDouble(Marker::distanceSquared));
            markers = List.copyOf(workingMarkers);
            workingMarkers = null;
            loadedChunks = null;
            ticksUntilRescan = RESCAN_DELAY_TICKS;
        }
    }

    private static double originDistanceSquared(BlockPos pos) {
        double dx = pos.getX() - scanOriginX;
        double dy = pos.getY() - scanOriginY;
        double dz = pos.getZ() - scanOriginZ;
        return dx * dx + dy * dy + dz * dz;
    }

    public static List<Marker> getMarkers() {
        return markers;
    }

    public static boolean isScanning() {
        return workingMarkers != null;
    }

    public static void clear() {
        markers = List.of();
        workingMarkers = null;
        loadedChunks = null;
        nextDx = -RANGE;
        ticksUntilRescan = 0;
    }
}
