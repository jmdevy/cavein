# Minecraft Cave in mod
#WARNING: there are no guarantees that this mod will not grief your world beyond your liking. Make a backup.

This is a Minecraft 1.19.2 Forge mod that adds cave ins underground.

At certain intervals a cave in may randomly start for one player in the overworld. Whitelisted blocks will fall and hurt players during a cave in. Cave ins will only start under a certain height.

## Configuration

Certain aspects of the mod can be reconfigured in the .minecraft/config/cavein-common.toml config file.

The default configuration potentially starts a cave in every 5 minutes at a 1 in 30 chance. The cave ins occupy a 30 block radius by default, and will only occur under a block height of 50. See the referenced config file for other options.

## Building
1. `git clone https://github.com/jmdevy/cavein.git`
2. Open `cavein/build.gradle` as a project in IntelliJ IDEA (Community Edition) IDE
3. In terminal run `./gradlew genIntellijRuns`
4. In IDE on side panel where it says "Gradle" expand the panel and double click Tasks/build/build to build the ready to serve .jar. The .jar can be found in `cavein/build/libs/cave-x.x.jar`

During development, use the `runClient` and `runServer` configurations from the top-right dropdown to quickly run the game with the mod installed.

---

About
==================
This is a Minecraft 1.19.2 Forge mod that adds cave ins underground.

When a cave in randomly occurs, blocks will fall and potentially bury players and mobs. Cave ins can also happen in underwater caves!

Installing
----------------
This needs to be installed on both the client and the server. Place the .jar file in the 'mods' folder on both the client and server.

Placing the .jar on the server only matters if you have a dedicated one, you don't have to do this if playing singleplayer or LAN.

Configuration
----------------
By default, there is a 1 in 30 chance that a cave in will occur every 5 minutes for a single player in the overworld. Cave ins will only occur below Y level 50 and only whitelisted common blocks like stone, granite, dirt, etc. will fall.

Most features of this mod can be adjusted in the config file located at .minecraft/config/cavein-common.toml.
