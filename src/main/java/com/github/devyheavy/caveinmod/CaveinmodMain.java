package com.github.devyheavy.caveinmod;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CaveinmodMain.MODID)
public class CaveinmodMain {
    public static final String MODID = "caveinmod";

    private static final Logger LOGGER = LogManager.getLogger();

    // Constructor (print when forge launches before being at the Minecraft main screen)
    public CaveinmodMain() {
        LOGGER.debug("Hello World! From the cave-in mod");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CaveinmodConfigHandler.CAVEINMOD_CLIENT_CONFIG_SPEC);
    }
}
