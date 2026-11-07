# Donut Visual Client 1.6.1

A client-side Fabric mod for Minecraft Java 26.2.

## Features

- Right Shift opens the purple Click GUI.
- Brightness Boost sets the vanilla gamma option to its supported maximum.
- No Entity Shadows toggles Minecraft's entity-shadow option.
- Storage ESP highlights nearby chest, trapped chest, barrel, ender chest, and shulker-box blocks with purple boxes and lists the nearest coordinates in the HUD.
- Storage ESP scans up to a 200-block horizontal radius and 128 blocks above/below the player. It scans only already-loaded chunks and spreads work over multiple ticks to limit lag.
- Freecam has independent mouse-look and WASD flight. Space rises, Shift descends, and Sprint increases speed.

## Private-world restriction

Storage ESP and Freecam are enabled only when an integrated single-player server is active. The storage scan never requests or generates chunks. Both tools are automatically disabled outside a local single-player world.

These scouting modules are intentionally unavailable on Donut SMP or other multiplayer servers.

## Controls

- Right Shift: open GUI
- F6: toggle Freecam (single-player only)
- F7: toggle Storage ESP (single-player only)
- WASD: move the freecam
- Mouse: look around
- Space / Shift: move up / down
- Sprint: move faster

The Click GUI also contains buttons for all modules.

## Build

- Minecraft 26.2
- Java 25
- Fabric Loader 0.19.3+
- Fabric API 0.152.1+26.2
- Fabric Loom 1.17.21
- Gradle 9.5.1

Run gradle clean build. The JAR appears in build/libs.

A successful CI build checks compilation and packaging. Test the mod in a separate Minecraft profile before using it in a saved world.
