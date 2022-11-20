package jmdevy.caveins.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

// Handles getting and storing settings for client with file minecraft/config
public class ClientConfigHandler {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG_SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_CONFIG = specPair.getLeft();
        CLIENT_CONFIG_SPEC = specPair.getRight();
    }
}
