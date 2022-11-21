package jmdevy.cavein.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

// Handles getting and storing settings for server with file .minecraft/world/serverconfig
public class CommonConfigHandler {
    public static CommonConfig COMMON_CONFIG = null;
    public static ForgeConfigSpec COMMON_CONFIG_SPEC = null;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_CONFIG = specPair.getLeft();
        COMMON_CONFIG_SPEC = specPair.getRight();
    }
}
