# Cave in mod
This is a Minecraft 1.19.2 Forge mod that adds cave ins underground.

At certain intervals a cave in may randomly start for one player in the overworld. Whitelisted blocks will fall and hurt players during a cave in. Cave ins will only start under a certain height.

## Configuration

Certain aspects of the mod can be reconfigured in the .minecraft/config/cavein-common.toml config file.

## Building
1. `git clone https://github.com/jmdevy/cavein.git`
2. Open in `cavein/build.gradle` as a project in IntelliJ IDEA (Community Edition) IDE
3. In terminal run `./gradlew genIntellijRuns`
4. In IDE on side panel where it says "Gradle" expand the panel and double click Tasks/build/build to build the ready to serve .jar. The .jar can be found in `cavein/build/libs/cave-x.x.jar`

During development, use the `runClient` and `runServer` configurations from the top-right dropdown to quickly run the game with the mod installed.