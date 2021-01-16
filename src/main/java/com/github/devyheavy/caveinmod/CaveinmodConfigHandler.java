package com.github.devyheavy.caveinmod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = CaveinmodMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CaveinmodConfigHandler {
    public static final ClientConfig CAVEINMOD_CLIENT_CONFIG;
    public static final ForgeConfigSpec CAVEINMOD_CLIENT_CONFIG_SPEC;
    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CAVEINMOD_CLIENT_CONFIG_SPEC = specPair.getRight();
        CAVEINMOD_CLIENT_CONFIG = specPair.getLeft();
    }


    public static Double updateRateSeconds;         // How many seconds between doing tasks in mod
    public static Double minSecondsToCavein;        // Low end of range until a cave-in can occur in seconds
    public static Double maxSecondsToCavein;        // Hi end of range until a cave-in can occur in seconds
    public static Integer maxCaveinYLevel;          // The highest cave-ins can appear for any player


    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == CaveinmodConfigHandler.CAVEINMOD_CLIENT_CONFIG_SPEC) {
            bakeConfig();
        }
    }


    public static void bakeConfig() {
        updateRateSeconds = CAVEINMOD_CLIENT_CONFIG.updateRateSeconds.get();
        minSecondsToCavein = CAVEINMOD_CLIENT_CONFIG.minSecondsToCavein.get();
        maxSecondsToCavein = CAVEINMOD_CLIENT_CONFIG.maxSecondsToCavein.get();
        maxCaveinYLevel = CAVEINMOD_CLIENT_CONFIG.maxCaveinYLevel.get();
    }


    public static class ClientConfig {
        public final ForgeConfigSpec.ConfigValue<Double> updateRateSeconds;         // How many seconds between doing tasks in mod
        public final ForgeConfigSpec.ConfigValue<Double> minSecondsToCavein;        // Low end of range until a cave-in can occur in seconds
        public final ForgeConfigSpec.ConfigValue<Double> maxSecondsToCavein;        // Hi end of range until a cave-in can occur in seconds
        public final ForgeConfigSpec.ConfigValue<Integer> maxCaveinYLevel;          // The highest cave-ins can appear for any player

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Timings");
            updateRateSeconds = builder
                    .comment("aBoolean usage description")
                    .translation(CaveinmodMain.MODID + ".config." + "updateRateSeconds")
                    .define("updateRateSeconds", 0.05);
            minSecondsToCavein = builder
                    .comment("anInt usage description")
                    .translation(CaveinmodMain.MODID + ".config." + "minSecondsToCavein")
                    .define("minSecondsToCavein", 0.25);
            maxSecondsToCavein = builder
                    .comment("anInt usage description")
                    .translation(CaveinmodMain.MODID + ".config." + "maxSecondsToCavein")
                    .define("maxSecondsToCavein", 1.0);
            maxCaveinYLevel = builder
                    .comment("anInt usage description")
                    .translation(CaveinmodMain.MODID + ".config." + "maxCaveinYLevel")
                    .define("maxCaveinYLevel", 60);
            builder.pop();
        }

    }
}
