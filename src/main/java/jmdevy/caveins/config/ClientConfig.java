package jmdevy.caveins.config;

import jmdevy.caveins.Caveins;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("NotSettings");
        builder.comment("Nothing here that can be controlled by clients. Look in .minecraft/world/serverconfig for configs controllable by the server")
                .translation(Caveins.MODID + ".config." + "Nothing")
                .define("Nothing", 0);
        builder.pop();
    }
}
