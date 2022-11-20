package jmdevy.caveins;

import com.mojang.logging.LogUtils;
import jmdevy.caveins.config.ClientConfigHandler;
import jmdevy.caveins.config.ServerConfigHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Caveins.MODID)
public class Caveins {
    public static final String MODID = "caveins";

    private static final Logger LOGGER = LogUtils.getLogger();

    public Caveins() {
        LOGGER.debug("Hello World! From the cave ins mod!");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigHandler.CLIENT_CONFIG_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigHandler.SERVER_CONFIG_SPEC);
    }
}
