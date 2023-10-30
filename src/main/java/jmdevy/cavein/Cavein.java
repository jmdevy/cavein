package jmdevy.cavein;

import com.mojang.logging.LogUtils;
import jmdevy.cavein.config.CommonConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// Entry point of mod

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Cavein.MODID)
public class Cavein {
    public static final String MODID = "cavein";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final int ticksPerSecond = 20;

    public Cavein() {
        LOGGER.debug("Cave in mod loaded!");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigHandler.COMMON_CONFIG_SPEC);

        // Instead of using decorator, register like this so that it's used on client?
        MinecraftForge.EVENT_BUS.register(Server.class);
    }
}
