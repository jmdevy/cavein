package com.github.devyheavy.caveinmod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = CaveinmodMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CaveinmodConfigHandler {

    // ##### START OF COMMON (SERVER TO CLIENT) CONFIG (STORED IN .minecraft/world/serverconfig) #####
    public static final CaveinModCommonConfig CAVEINMOD_COMMON_CONFIG;
    public static final ForgeConfigSpec CAVEINMOD_COMMON_CONFIG_SPEC;
    static {
        final Pair<CaveinModCommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CaveinModCommonConfig::new);
        CAVEINMOD_COMMON_CONFIG_SPEC = specPair.getRight();
        CAVEINMOD_COMMON_CONFIG = specPair.getLeft();
    }


    private static Double updateRateSeconds;         // How many seconds between doing tasks in mod
    private static Double minSecondsToCavein;        // Low end of range until a cave-in can occur in seconds
    private static Double maxSecondsToCavein;        // Hi end of range until a cave-in can occur in seconds
    public static Integer maxCaveinYLevel;          // The highest cave-ins can appear for any player

    // Items resulting from config values
    public static int TICKS_PER_SECOND = 20; // How many server ticks in each second (20 Hz)
    public static int updateRateTicks;       // Number of server ticks between updates (truncated for int)
    public static int minTicksToCavein;      // Low end of range until a cave-in can occur in ticks
    public static int maxTicksToCavein;      // Hi end of range until a cave-in can occur in ticks


    public static void CaveinModBakeCommonConfig() {
        updateRateSeconds = CAVEINMOD_COMMON_CONFIG.updateRateSeconds.get();
        minSecondsToCavein = CAVEINMOD_COMMON_CONFIG.minSecondsToCavein.get();
        maxSecondsToCavein = CAVEINMOD_COMMON_CONFIG.maxSecondsToCavein.get();
        maxCaveinYLevel = CAVEINMOD_COMMON_CONFIG.maxCaveinYLevel.get();

        // Do some conversions so other classes do not have to
        updateRateTicks = (int) (TICKS_PER_SECOND * updateRateSeconds);
        minTicksToCavein = (int) (TICKS_PER_SECOND * minSecondsToCavein);
        maxTicksToCavein = (int) (TICKS_PER_SECOND * maxSecondsToCavein);
    }


    public static class CaveinModCommonConfig {
        public final ForgeConfigSpec.ConfigValue<Double> updateRateSeconds;         // How many seconds between doing tasks in mod
        public final ForgeConfigSpec.ConfigValue<Double> minSecondsToCavein;        // Low end of range until a cave-in can occur in seconds
        public final ForgeConfigSpec.ConfigValue<Double> maxSecondsToCavein;        // Hi end of range until a cave-in can occur in seconds
        public final ForgeConfigSpec.ConfigValue<Integer> maxCaveinYLevel;          // The highest cave-ins can appear for any player

        public CaveinModCommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Timings and general");
            updateRateSeconds = builder
                    .comment("Decimal or double value for amount of seconds between making blocks fall during caveins (larger values reduce lag/tps lag/fps lag/updates)")
                    .translation(CaveinmodMain.MODID + ".config." + "updateRateSeconds")
                    .define("updateRateSeconds", 0.05);

            minSecondsToCavein = builder
                    .comment("Decimal or double value for bottom range of number of seconds until a cave-in can randomly occur")
                    .translation(CaveinmodMain.MODID + ".config." + "minSecondsToCavein")
                    .define("minSecondsToCavein", 10.25);

            maxSecondsToCavein = builder
                    .comment("Decimal or double value for upper range of a number of seconds until a cave-in can randomly occur")
                    .translation(CaveinmodMain.MODID + ".config." + "maxSecondsToCavein")
                    .define("maxSecondsToCavein", 21.0);

            maxCaveinYLevel = builder
                    .comment("Integer or int value for height limit where cave-ins can occur. Cave-ins will not modify blocks above or equal to this height limit")
                    .translation(CaveinmodMain.MODID + ".config." + "maxCaveinYLevel")
                    .define("maxCaveinYLevel", 60);
            builder.pop();
        }
    }


    // ##### START OF CLIENT (ONLY CLIENT) CONFIG (STORED IN .minecraft/config) #####
    public static final CaveinModClientConfig CAVEINMOD_CLIENT_CONFIG;
    public static final ForgeConfigSpec CAVEINMOD_CLIENT_CONFIG_SPEC;
    static {
        final Pair<CaveinModClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CaveinModClientConfig::new);
        CAVEINMOD_CLIENT_CONFIG_SPEC = specPair.getRight();
        CAVEINMOD_CLIENT_CONFIG = specPair.getLeft();
    }


    // Bake configs to standard values in this class since using get() and set() is costly
    public static void CaveinModBakeClientConfig() {
    }


    public static class CaveinModClientConfig {
        public CaveinModClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("NotSettings");
            builder.comment("Nothing here that can be controlled by clients. Look in .minecraft/world/serverconfig for configs controllable by the server")
                    .translation(CaveinmodMain.MODID + ".config." + "Nothing")
                    .define("Nothing", 0);
            builder.pop();
        }
    }



    // HANDLE CONFIG EVENT FOR BOTH CLIENT AND SERVER-CLIENT CONFIG EVENTS
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == CaveinmodConfigHandler.CAVEINMOD_COMMON_CONFIG_SPEC) {
            CaveinModBakeCommonConfig();
        }
        if (configEvent.getConfig().getSpec() == CaveinmodConfigHandler.CAVEINMOD_CLIENT_CONFIG_SPEC) {
            CaveinModBakeClientConfig();
        }
    }
}
