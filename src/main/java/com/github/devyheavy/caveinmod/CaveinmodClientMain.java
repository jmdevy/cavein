package com.github.devyheavy.caveinmod;


import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;            // @EventBusSubscriber
import net.minecraftforge.eventbus.api.SubscribeEvent;                  // @SubscribeEvent

// NOTE: -Mod-specific events like Registry events and Mod Loading events are fired on the 'MOD' event bus
//       -Non-mod-specific events such as Tick events, Rendering events, World events and Player events are fired on the 'FORGE' event bus.
//       *Only events (i.e. objects whose class extends net.minecraftforge.eventbus.api.Event) can be listened for/subscribed to on the Event Buses.
@EventBusSubscriber(modid = CaveinmodMain.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CaveinmodClientMain {


    //
    // Default itmes resulting from config values (set using data from server in client packet handler)
    //
    private static int updateRateTicks;             // Number of server ticks between updates (truncated for int)
    private static int minTicksToCavein;            // Low end of range until a cave-in can occur in ticks
    private static int maxTicksToCavein;            // Hi end of range until a cave-in can occur in ticks
    private static int maxCaveinYLevel;             // The highest cave-ins can appear for any player


    //
    // Internal items used for for tracking
    //
    private static int ticksPassedToUpdate = 0;     // Number of ticks that have passed since last update (reset to 0 every updateRateTicks ticks)
    private static int currentTicksToCavein = 0;    // Number randomly assigned to number in range of minTicksToCavein & maxTicksToCavein
    private static int ticksPassedToCavein = -1;    // Related to minTicksToCavein & maxTicksToCavein and is used to count to random number in that range


    //
    // Mostly used to set client-side config values when received from server
    // in CaveinmodMessagehandlerOnClient sent from CaveinmodServerMain
    //
    public static void setUpdateRateTicks(int i_updateRateTicks){
        updateRateTicks = i_updateRateTicks;
    }
    public static void setMinTicksToCavein(int i_minTicksToCavein){
        minTicksToCavein = i_minTicksToCavein;
    }
    public static void setMaxTicksToCavein(int i_maxTicksToCavein){
        maxTicksToCavein = i_maxTicksToCavein;
    }
    public static void setMaxCaveinYLevel(int i_maxCaveinYLevel){
        maxTicksToCavein = i_maxCaveinYLevel;
    }


    @SubscribeEvent
    public static void CaveinUpdate(TickEvent.ClientTickEvent event) {
        // Don't do anything if the player does not exist since user is in main menu of Minecraft
        if(Minecraft.getInstance().player == null){
            return;
        }

        // Only update ticks at end of phase (between START & END a snap-back due to poor TPS can happen)
        // and this ensures the blocks are the most up to date when picking ones to fall
        if(event.phase == TickEvent.Phase.END) {
            // Only update client tracking of cave-ins every updateRateTicks set by mod user
            if(ticksPassedToUpdate >= updateRateTicks) {

                // Chance of cave-in can occur now
                if(ticksPassedToCavein >= currentTicksToCavein){
                    // Last check, only do cave-ins if player is below max limit
                    if(Minecraft.getInstance().player.getPosition().getY() < maxCaveinYLevel) {
                        CaveinmodMessageToServer caveinMessageToServer = new CaveinmodMessageToServer(10);
                        CaveinmodPacketHandler.simpleChannel.sendToServer(caveinMessageToServer);
                        currentTicksToCavein = (int) ((Math.random() * (maxTicksToCavein - minTicksToCavein)) + minTicksToCavein);  // New amount of ticks until cave-in chance
                        ticksPassedToCavein = 0;    // Cave-in had chance to occur, reset
                        //System.out.println(Minecraft.getInstance().player.getName().getString());
                    }
                }
                ticksPassedToUpdate = 0;    // Update occured, reset
            }
            ticksPassedToUpdate++;  // Reset every updateRateTicks (partially dictates ticksPassedToCavein)
            ticksPassedToCavein++;  // Reset every currentTicksToCavein but depends on ticksPassedToUpdate
        }
    }
}
