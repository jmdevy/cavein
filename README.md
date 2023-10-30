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

## Updating Sourcecode and Forge

1. Visit https://docs.minecraftforge.net/en/1.20.x/gettingstarted/
2. Download MDk from https://files.minecraftforge.net/net/minecraftforge/forge/
3. Delete and replace the following from the downloaded MDK
    1. Delete the following from this repository and copy replacements from MDK:
       1. `gradle` directory
       2. `build.gradle`
       3. `gradlew`
       4. `gradlew.bat`
   2. Set the following in `build.gradle` (tends to change from Forge version to Forge version though):
       1. `version = '1.20.2-1.1.1'` (the ending version scheme is major.minor.patch)
       2. `group = 'jmdevy'`
       3. ```
          base {
              archivesName = 'cavein'
          }
          ```
       4. `java.toolchain.languageVersion = JavaLanguageVersion.of(17)`
       5. In `mincraft{` set `mappings channel: 'offcial', version: '1.20.2'`
       6. In `client{` set `mod_id` to `'${mod_id}'`
       7. In `server{` set `mod_id` to `'${mod_id}'`
       8. In `gameTestServer{` set `mod_id` to `'${mod_id}'`
       9. In `data{` and `args` set `mod_id` to `'${mod_id}'`
      10. In `dependencies` and `minecraft` set `${minecraft_version}` to `1.20.2`
      11. In `dependencies` and `minecraft` set `${forge_version}` to `48.0.30` (whatever Forge version updating to)
      12. In `attributes([` set the following
          1. `'Specification-Title'     : "cavein"`
          2. `"Specification-Vendor"    : "jmdevy"`
          3. `"Implementation-Vendor"   : "jmdevy"`
      13. Remove `tasks.named('processResources', ProcessResources).configure` block of code
   3. Set the following in `src/main/resources/META-INF/mods.toml`
      1. Set `loaderVersion="[48,)"` (or whatever the latest major version of Forge is)
      2. Set `versionRange="[48,)"` in `[[dependencies.cavein]]`
      3. Set `versionRange="[1.20.2,)"` in `[[dependencies.cavein]]`