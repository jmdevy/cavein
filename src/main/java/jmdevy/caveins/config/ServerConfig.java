package jmdevy.caveins.config;

import jmdevy.caveins.Caveins;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public final ForgeConfigSpec.ConfigValue<Double> updateRateSeconds;         // How many seconds between doing tasks in mod
    public final ForgeConfigSpec.ConfigValue<Double> minSecondsToCavein;        // Low end of range until a cave-in can occur in seconds
    public final ForgeConfigSpec.ConfigValue<Double> maxSecondsToCavein;        // Hi end of range until a cave-in can occur in seconds
    public final ForgeConfigSpec.ConfigValue<Integer> maxCaveinYLevel;          // The highest cave-ins can appear for any player

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Timings and general");
        updateRateSeconds = builder
                .comment("Decimal or double value for amount of seconds between making blocks fall during caveins (larger values reduce lag/tps lag/fps lag/updates)")
                .translation(Caveins.MODID + ".config." + "updateRateSeconds")
                .define("updateRateSeconds", 0.05);

        minSecondsToCavein = builder
                .comment("Decimal or double value for bottom range of number of seconds until a cave-in can randomly occur")
                .translation(Caveins.MODID + ".config." + "minSecondsToCavein")
                .define("minSecondsToCavein", 10.25);

        maxSecondsToCavein = builder
                .comment("Decimal or double value for upper range of a number of seconds until a cave-in can randomly occur")
                .translation(Caveins.MODID + ".config." + "maxSecondsToCavein")
                .define("maxSecondsToCavein", 21.0);

        maxCaveinYLevel = builder
                .comment("Integer or int value for height limit where cave-ins can occur. Cave-ins will not modify blocks above or equal to this height limit")
                .translation(Caveins.MODID + ".config." + "maxCaveinYLevel")
                .define("maxCaveinYLevel", 60);
        builder.pop();
    }
}
