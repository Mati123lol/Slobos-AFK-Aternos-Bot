# Donut Visual Client 1.7.0

A client-side Fabric mod for Minecraft Java 26.2.

## Features

- Right Shift opens the purple Click GUI.
- Brightness Boost sets the vanilla gamma option to its supported maximum.
- No Entity Shadows toggles Minecraft's entity-shadow option.
- Storage ESP highlights chest, trapped chest, barrel, ender chest, and shulker-box blocks with purple boxes and lists the nearest coordinates in the HUD.
- Storage ESP scans a 200-block horizontal radius and 128 blocks above/below the player, but only in chunks already loaded and sent to the client. The scan is spread over multiple ticks.
- Freecam is a detached client-side camera with independent mouse-look and WASD flight. Space rises, Shift descends, and Sprint increases speed.

## Multiplayer behavior

Storage ESP and the detached local camera can be toggled in single-player and multiplayer worlds. Use them only on servers you own or where the server owner has explicitly permitted them.

The client cannot request or generate chunks, bypass anti-cheat, or reveal blocks in chunks the server has not sent to the client. In multiplayer, the freecam does not move the actual server-side player and is limited to the chunks the client already has loaded around that player. Server view distance and client render distance affect how far the camera can see.

## Controls

- Right Shift: open GUI
- F6: toggle Freecam
- F7: toggle Storage ESP
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
