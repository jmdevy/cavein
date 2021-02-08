package com.github.devyheavy.caveinmod;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;                  // @SubscribeEvent
import net.minecraftforge.fml.common.Mod;

import java.util.Vector;

@Mod.EventBusSubscriber(modid = CaveinmodMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CaveinmodServerMain {


    // Config options (MOVE TO JSON)
    final private static float updateRateSeconds = 0.05f;       // How many seconds between doing tasks in mod
    final private static float minSecondsToCavein = 0.25f;      // Low end of range until a cave-in can occur in seconds
    final private static float maxSecondsToCavein = 1;          // Hi end of range until a cave-in can occur in seconds
    final private static int maxCaveinYLevel = 60;              // The highest cave-ins can appear for any player


    // Items resulting from config values
    final private static int ticksPerSecond = 20;                                               // How many server ticks in each second (20 Hz)
    final private static int updateRateTicks = (int) (ticksPerSecond * updateRateSeconds);      // Number of server ticks between updates (truncated for int)
    final private static int minTicksToCavein = (int) (ticksPerSecond * minSecondsToCavein);    // Low end of range until a cave-in can occur in ticks
    final private static int maxTicksToCavein = (int) (ticksPerSecond * maxSecondsToCavein);    // Hi end of range until a cave-in can occur in ticks


    // Internal items of the class not related to config values
    private static Vector<CaveinmodCaveinInstance> activeCaveinInstances = new Vector<CaveinmodCaveinInstance>();   // List of cave-ins added from server
                                                                                                                    // processing client messages
    private static int currentActiveCaveinInstanceIndex = 0;    // Used to track what cave-in instance is currently being processed

    // Used by CaveinmodMessagehandlerOnServer to add cave-ins sent from clients to this class
    public static void AddCaveinInstanceToActive(CaveinmodCaveinInstance _newActiveInstance) {
        activeCaveinInstances.add(_newActiveInstance);
    }


    // Only handle one active cave-in per tick, don't want to slow the server
    @SubscribeEvent
    public static void CaveinmodHanldeActiveCaveins(TickEvent.ServerTickEvent event) {
        // Only update ticks at end of phase (between START & END a snap-back due to poor TPS can happen)
        // and this ensures the blocks are the most up to date when picking ones to fall
        if(event.phase == TickEvent.Phase.END) {
            if(activeCaveinInstances.size() > 0 && currentActiveCaveinInstanceIndex < activeCaveinInstances.size()) {
                // Server main only tells the cave-in instance to process the blocks and tracks instances of cave-ins
                CaveinmodCaveinInstance currentCaveinInstance = activeCaveinInstances.get(currentActiveCaveinInstanceIndex);
                currentCaveinInstance.processCavein();

                // When cave-in instance has been processed to its duration, remove from active lista nd tell all
                // the clients to stop shaking their camera when close to that position
                if(currentCaveinInstance.getCurrentCaveinTickCount() >= currentCaveinInstance.getCaveinTicksDuration()){
                    activeCaveinInstances.remove(currentActiveCaveinInstanceIndex);
                }
            } else {
                currentActiveCaveinInstanceIndex = 0;   // Reset this since all cave-ins are over or the index is out of bounds
            }
        }
    }


//    @SubscribeEvent
//    public static void sendConfigsToClient(EntityJoinWorldEvent event) {
////        // Ensure new entity is player then send server config data
////        if ((event.getEntity() instanceof ServerPlayerEntity)) {
////            // If first player join event (ignoringSecondJoinEvent == false), send data to client
////            if(ignoringSecondJoinEvent == false){
////                ignoringSecondJoinEvent = true;     // Logic for ignoring second firing of this event
////                CaveinmodMessageToClient configMessageToClient = new CaveinmodMessageToClient(updateRateTicks, minTicksToCavein, maxTicksToCavein, maxCaveinYLevel);
////                ServerPlayerEntity targetPlayer = (ServerPlayerEntity) event.getEntity();
////                CaveinmodPacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> targetPlayer), configMessageToClient);
////            } else {
////                ignoringSecondJoinEvent = false;     // Logic for ignoring second firing of this event
////            }
////        }
//    }
}
