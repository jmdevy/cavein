package jmdevy.cavein.config;

import jmdevy.cavein.Cavein;
import net.minecraftforge.common.ForgeConfigSpec;


public class CommonConfig {
    public final ForgeConfigSpec.ConfigValue<String> whitelistedBlocks;

    public final ForgeConfigSpec.ConfigValue<Integer> caveinRadius;             // Radius of area of that cave in occurs in
    public final ForgeConfigSpec.ConfigValue<Integer> caveinHeightBelow;        // How many blocks below the player to search for cave-able blocks
    public final ForgeConfigSpec.ConfigValue<Integer> caveinHeightAbove;        // How many blocks above the player to search for cave-able blocks
    public final ForgeConfigSpec.ConfigValue<Integer> secondsToCavein;          // Seconds until a cave-in can potentially be triggered on client
    public final ForgeConfigSpec.ConfigValue<Integer> caveinChance;             // Chance of a cave-in occurring every secondsToCavein per player
    public final ForgeConfigSpec.ConfigValue<Integer> maxCaveinYLevel;          // The highest cave-ins can appear for any player

    public final ForgeConfigSpec.ConfigValue<Integer> minCaveinDurationSeconds;
    public final ForgeConfigSpec.ConfigValue<Integer> maxCaveinDurationSeconds;

    public final ForgeConfigSpec.ConfigValue<Double> relativeShakeAmount;


    public CommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Timings and general");
        whitelistedBlocks = builder
                .comment("Integer or int value for number of seconds until a cave-in can randomly occur per player")
                .translation(Cavein.MODID + ".config." + "whitelistedBlocks")
                .define("whitelistedBlocks", "[minecraft:stone,minecraft:dirt,minecraft:andesite,minecraft:diorite,minecraft:dripstone_block,minecraft:pointed_dripstone,minecraft:granite,minecraft:deepslate,minecraft:iron_ore,minecraft:gold_ore,minecraft:coal_ore,minecraft:copper_ore,minecraft:redstone_ore,minecraft:deepslate_iron_ore,minecraft:deepslate_gold_ore,minecraft:deepslate_coal_ore,minecraft:deepslate_copper_ore,minecraft:deepslate_redstone_ore]");

        caveinRadius = builder
                .comment("Integer or int value of radius that cave in occurs in (unit of blocks)")
                .translation(Cavein.MODID + ".config." + "caveinRadius")
                .define("caveinRadius", 32);

        caveinHeightBelow = builder
                .comment("Integer or int value of how many blocks below the player to search for cave-able blocks")
                .translation(Cavein.MODID + ".config." + "caveinHeightBelow")
                .define("caveinHeightBelow", 4);

        caveinHeightAbove = builder
                .comment("Integer or int value of how many blocks above the player to search for cave-able blocks")
                .translation(Cavein.MODID + ".config." + "caveinHeightAbove")
                .define("caveinHeightAbove", 20);

        secondsToCavein = builder
                .comment("Integer or int value for number of seconds until a cave-in can randomly occur per player")
                .translation(Cavein.MODID + ".config." + "secondsToCavein")
                .define("secondsToCavein", 5);

        caveinChance = builder
                .comment("Integer or int value for chance of a cave-in occurring per player (for example, setting to 100 means a 1 in 100 chance of a cave-in every secondsToCavein per player)")
                .translation(Cavein.MODID + ".config." + "caveinChance")
                .define("caveinChance", 2);

        maxCaveinYLevel = builder
                .comment("Integer or int value for height limit where cave-ins can occur. Cave-ins will not modify blocks above or equal to this height limit")
                .translation(Cavein.MODID + ".config." + "maxCaveinYLevel")
                .define("maxCaveinYLevel", 60);


        minCaveinDurationSeconds = builder
                .comment("Integer or int value of minimum number of seconds a cave in can last")
                .translation(Cavein.MODID + ".config." + "minCaveinDurationSeconds")
                .define("minCaveinDurationSeconds", 15);

        maxCaveinDurationSeconds = builder
                .comment("Integer or int value of maximum number of seconds a cave in can last")
                .translation(Cavein.MODID + ".config." + "maxCaveinDurationSeconds")
                .define("maxCaveinDurationSeconds", 60);

        relativeShakeAmount = builder
                .comment("Double or decimal value of how much a player should shake during a cave in (only adjusts the player's position)")
                .translation(Cavein.MODID + ".config." + "relativeShakeAmount")
                .define("relativeShakeAmount", 0.75);

        builder.pop();
    }
}
