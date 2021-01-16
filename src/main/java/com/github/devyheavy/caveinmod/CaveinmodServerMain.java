package com.github.devyheavy.caveinmod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;            // @EventBusSubscriber
import net.minecraftforge.eventbus.api.SubscribeEvent;                  // @SubscribeEvent
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = CaveinmodMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CaveinmodServerMain {


    //
    // Config options (MOVE TO JSON)
    //
    final private static float updateRateSeconds = 0.05f;       // How many seconds between doing tasks in mod
    final private static float minSecondsToCavein = 0.25f;      // Low end of range until a cave-in can occur in seconds
    final private static float maxSecondsToCavein = 1;          // Hi end of range until a cave-in can occur in seconds
    final private static int maxCaveinYLevel = 60;              // The highest cave-ins can appear for any player


    //
    // Items resulting from config values
    //
    final private static int ticksPerSecond = 20;                                               // How many server ticks in each second (20 Hz)
    final private static int updateRateTicks = (int) (ticksPerSecond * updateRateSeconds);      // Number of server ticks between updates (truncated for int)
    final private static int minTicksToCavein = (int) (ticksPerSecond * minSecondsToCavein);    // Low end of range until a cave-in can occur in ticks
    final private static int maxTicksToCavein = (int) (ticksPerSecond * maxSecondsToCavein);    // Hi end of range until a cave-in can occur in ticks


    //
    // Internal items of the class not related to config values
    //
    private static boolean ignoringSecondJoinEvent = false;     // Join event is fired twice when player joins, ignore second event


    @SubscribeEvent
    public static void sendConfigsToClient(EntityJoinWorldEvent event) {
        System.out.println("SEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        System.out.println(CaveinmodConfigHandler.minSecondsToCavein);
        // Ensure new entity is player then send server config data
        if ((event.getEntity() instanceof ServerPlayerEntity)) {
            // If first player join event (ignoringSecondJoinEvent == false), send data to client
            if(ignoringSecondJoinEvent == false){
                ignoringSecondJoinEvent = true;     // Logic for ignoring second firing of this event
                CaveinmodMessageToClient configMessageToClient = new CaveinmodMessageToClient(updateRateTicks, minTicksToCavein, maxTicksToCavein, maxCaveinYLevel);
                ServerPlayerEntity targetPlayer = (ServerPlayerEntity) event.getEntity();
                CaveinmodPacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> targetPlayer), configMessageToClient);
            } else {
                ignoringSecondJoinEvent = false;     // Logic for ignoring second firing of this event
            }
        }
    }
}
