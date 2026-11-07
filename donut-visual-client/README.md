# Donut Visual Client 1.5.0

A client-side Fabric mod for Minecraft Java 26.2.

## Features

- Right Shift opens the purple Click GUI.
- Brightness Boost sets the vanilla gamma option to its supported maximum.
- No Entity Shadows toggles Minecraft's entity-shadow option.
- Storage ESP shows nearby chest, trapped chest, barrel, ender chest, and shulker-box coordinates in a HUD panel and draws purple highlighted boxes around them.
- Freecam detaches the camera and supports smooth WASD flight, Space to rise, Shift to descend, and Sprint to move faster.

## Private-world restriction

Storage ESP and Freecam are enabled only when an integrated single-player server is active. The storage scan checks already-loaded blocks only and does not request or generate chunks. Both tools are automatically disabled outside a local single-player world.

These two scouting modules are intentionally unavailable on Donut SMP or other multiplayer servers.

## Controls

- Right Shift: open GUI
- F6: toggle Freecam (single-player only)
- F7: toggle Storage ESP (single-player only)
- WASD: move the freecam
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
