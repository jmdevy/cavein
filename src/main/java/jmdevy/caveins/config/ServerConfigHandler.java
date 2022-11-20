package jmdevy.caveins.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

// Handles getting and storing settings for server with file .minecraft/world/serverconfig
public class ServerConfigHandler {
    public static ServerConfig SERVER_CONFIG = null;
    public static ForgeConfigSpec SERVER_CONFIG_SPEC = null;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPair.getLeft();
        SERVER_CONFIG_SPEC = specPair.getRight();
    }
}
