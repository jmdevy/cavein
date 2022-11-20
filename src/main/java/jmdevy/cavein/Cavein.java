package jmdevy.cavein;

import com.mojang.logging.LogUtils;
import jmdevy.cavein.config.ServerConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.List;

// Entry point of mod

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Cavein.MODID)
public class Cavein {
    public static final String MODID = "cavein";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final int ticksPerSecond = 20;

    public Cavein() {
        LOGGER.debug("Cave in mod loaded!");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigHandler.SERVER_CONFIG_SPEC);
    }
}
